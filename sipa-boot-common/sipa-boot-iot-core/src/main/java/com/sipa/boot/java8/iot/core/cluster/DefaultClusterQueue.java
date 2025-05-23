package com.sipa.boot.java8.iot.core.cluster;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;

import org.reactivestreams.Publisher;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterQueue;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultClusterQueue<T> implements IClusterQueue<T> {
    private static final Log log = LogFactory.get(DefaultClusterQueue.class);

    protected final ReactiveRedisOperations<String, T> operations;

    private final String id;

    private final AtomicBoolean polling = new AtomicBoolean(false);

    private final int maxBatchSize = 32;

    private volatile float localConsumerPercent = 1F;

    private final List<FluxSink<T>> subscribers = new CopyOnWriteArrayList<>();

    private long lastRequestSize = maxBatchSize;

    private EMod mod = EMod.FIFO;

    private final boolean useScript;

    @Override
    public void setLocalConsumerPercent(float localConsumerPercent) {
        this.localConsumerPercent = localConsumerPercent;
    }

    private static final RedisScript<List> lifoPollScript =
        RedisScript.of(String.join("\n", "local val = redis.call('lrange',KEYS[1],0,KEYS[2]);",
            "redis.call('ltrim',KEYS[1],KEYS[2]+1,-1);", "return val;"), List.class);

    private static final RedisScript<List> fifoPollScript =
        RedisScript.of(String.join("\n", "local size = redis.call('llen',KEYS[1]);", "if size == 0 then", "return nil",
            "end", "local index = size - KEYS[2];", "if index == 0 then", "return redis.call('lpop',KEYS[1]);", "end",
            "local val = redis.call('lrange',KEYS[1],index,size);", "redis.call('ltrim',KEYS[1],0,index-1);",
            "return val;"), List.class);

    private static final RedisScript<Long> pushAndPublish =
        RedisScript.of("local val = redis.call('lpush',KEYS[1],ARGV[1]);"
            + "redis.call('publish','queue:data:produced',ARGV[2]);" + "return val;", Long.class);

    public DefaultClusterQueue(String id, ReactiveRedisTemplate<String, T> operations) {
        this.id = id;
        this.operations = operations;

        LettuceConnectionFactory factory = (LettuceConnectionFactory)operations.getConnectionFactory();
        useScript = !factory.isClusterAware();
    }

    protected void tryPoll() {
        doPoll(lastRequestSize);
    }

    AtomicInteger lastPush = new AtomicInteger(0);

    private boolean push(Iterable<T> data) {
        for (T datum : data) {
            if (!push(datum)) {
                return false;
            }
        }
        return true;
    }

    private boolean push(T data) {
        int size = subscribers.size();
        if (size == 0) {
            return false;
        }
        if (size == 1) {
            subscribers.get(0).next(data);
            return true;
        }
        if (lastPush.incrementAndGet() >= size) {
            lastPush.set(0);
        }
        subscribers.get(lastPush.get()).next(data);
        return true;
    }

    private void doPoll(long size) {
        if (subscribers.size() <= 0) {
            return;
        }
        if (polling.compareAndSet(false, true)) {
            AtomicLong total = new AtomicLong(size);
            long pollSize = Math.min(total.get(), maxBatchSize);

            pollBatch((int)pollSize).flatMap(v -> {
                // 没有订阅者了,重入队列
                if (!push(v)) {
                    return operations.opsForList().leftPush(id, v).then();
                } else {
                    return Mono.just(v);
                }
            }).count().doFinally((s) -> polling.set(false)).subscribe(r -> {
                if (r > 0 && total.addAndGet(-r) > 0) { // 继续poll
                    polling.set(false);
                    doPoll(total.get());
                    log.trace("poll datas[{}] from redis [{}] ", r, id);
                }
            });
        }
    }

    protected void stopPoll() {
    }

    @Nonnull
    @Override
    public Flux<T> subscribe() {
        return Flux.<T>create(sink -> {
            subscribers.add(sink);
            sink.onDispose(() -> {
                subscribers.remove(sink);
            });
            doPoll(sink.requestedFromDownstream());
        }).doOnRequest(i -> {
            doPoll(lastRequestSize = i);
        });
    }

    @Override
    public void stop() {
        stopPoll();
    }

    @Override
    public Mono<Integer> size() {
        return operations.opsForList().size(id).map(Number::intValue);
    }

    @Override
    public void setPollMod(EMod mod) {
        this.mod = mod;
    }

    @Nonnull
    @Override
    public Mono<T> poll() {
        return mod == EMod.LIFO ? operations.opsForList().leftPop(id) : operations.opsForList().rightPop(id);
    }

    private Flux<T> pollBatch(int size) {
        if (size == 1 || !useScript) {
            return poll().flux();
        }
        return (mod == EMod.FIFO
            ? this.operations.execute(fifoPollScript, Arrays.asList(id, String.valueOf(size))).doOnNext(list -> {
                Collections.reverse(list); // 先进先出,反转顺序
            }) : this.operations.execute(lifoPollScript, Arrays.asList(id, String.valueOf(size))))
                .flatMap(Flux::fromIterable)
                .map(i -> (T)i);
    }

    private ReactiveRedisOperations getOperations() {
        return operations;
    }

    private boolean isLocalConsumer() {
        return subscribers.size() > 0
            && (localConsumerPercent == 1F || ThreadLocalRandom.current().nextFloat() < localConsumerPercent);
    }

    @Override
    public Mono<Boolean> add(Publisher<T> publisher) {
        return Flux.from(publisher).flatMap(v -> {
            if (isLocalConsumer() && push(v)) {
                return Mono.just(1);
            } else {
                return getOperations().execute(pushAndPublish, Arrays.asList(id), Arrays.asList(v, id));
            }
        }).then(Mono.just(true));
    }

    @Override
    public Mono<Boolean> addBatch(Publisher<? extends Collection<T>> publisher) {
        return Flux.from(publisher).flatMap(v -> {
            if (isLocalConsumer() && push(v)) {
                return Mono.just(1);
            }
            return this.operations.opsForList()
                .leftPushAll(id, v)
                .then(getOperations().convertAndSend("queue:data:produced", id));
        }).then(Mono.just(true));
    }
}
