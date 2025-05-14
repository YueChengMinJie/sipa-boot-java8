package com.sipa.boot.java8.iot.core.cluster;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.sipa.boot.java8.common.archs.cache.Caches;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.cluster.base.*;

import reactor.core.publisher.Flux;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultClusterManager implements IClusterManager {
    private static final Log log = LogFactory.get(DefaultClusterManager.class);

    private final String clusterName;

    private final String serverId;

    private final ReactiveRedisTemplate<Object, Object> commonOperations;

    private final Map<String, DefaultClusterQueue> queues = Caches.newCache();

    private final Map<String, IClusterTopic> topics = Caches.newCache();

    private final Map<String, IClusterCache> caches = Caches.newCache();

    private final Map<String, IClusterSet> sets = Caches.newCache();

    private final DefaultHaManager haManager;

    private final DefaultClusterNotifier notifier;

    private final ReactiveRedisOperations<String, String> stringOperations;

    private final ReactiveRedisTemplate<String, ?> queueRedisTemplate;

    public DefaultClusterManager(String name, ServerNode serverNode, ReactiveRedisTemplate<Object, Object> operations) {
        this.clusterName = name;
        this.commonOperations = operations;
        this.notifier = new DefaultClusterNotifier(name, serverNode.getId(), this);
        this.serverId = serverNode.getId();
        this.haManager = new DefaultHaManager(name, serverNode, this, (ReactiveRedisTemplate)operations);
        this.stringOperations =
            new ReactiveRedisTemplate<>(operations.getConnectionFactory(), RedisSerializationContext.string());

        this.queueRedisTemplate = new ReactiveRedisTemplate<>(operations.getConnectionFactory(),
            RedisSerializationContext.<String, Object>newSerializationContext()
                .key(RedisSerializer.string())
                .value(operations.getSerializationContext().getValueSerializationPair())
                .hashKey(RedisSerializer.string())
                .hashValue(operations.getSerializationContext().getHashValueSerializationPair())
                .build());
    }

    public DefaultClusterManager(String name, String serverId, ReactiveRedisTemplate<Object, Object> operations) {
        this(name, ServerNode.ServerNodeBuilder.aServerNode().withId(serverId).build(), operations);
    }

    @Override
    public String getCurrentServerId() {
        return serverId;
    }

    public void startup() {
        this.notifier.startup();
        this.haManager.startup();

        // 定时尝试拉取队列数据
        Flux.interval(Duration.ofSeconds(5))
            .flatMap(i -> Flux.fromIterable(queues.values()))
            .subscribe(DefaultClusterQueue::tryPoll);

        this.queueRedisTemplate.<String>listenToPattern("queue:data:produced").doOnError(err -> {
            log.error(err);
        }).subscribe(sub -> {
            DefaultClusterQueue queue = queues.get(sub.getMessage());
            if (queue != null) {
                queue.tryPoll();
            }
        });
    }

    public void shutdown() {
        this.haManager.shutdown();
    }

    @Override
    public IHaManager getHaManager() {
        return haManager;
    }

    protected <K, V> ReactiveRedisTemplate<K, V> getRedis() {
        return (ReactiveRedisTemplate<K, V>)commonOperations;
    }

    @Override
    public String getClusterName() {
        return clusterName;
    }

    @Override
    public IClusterNotifier getNotifier() {
        return notifier;
    }

    @Override
    public <T> IClusterQueue<T> getQueue(String queueId) {
        return queues.computeIfAbsent(queueId, id -> new DefaultClusterQueue<>(id, this.queueRedisTemplate));
    }

    @Override
    public <T> IClusterTopic<T> getTopic(String topic) {
        return topics.computeIfAbsent(topic, id -> new DefaultClusterTopic(id, this.getRedis()));
    }

    @Override
    public <K, V> IClusterCache<K, V> getCache(String cache) {
        return caches.computeIfAbsent(cache, id -> new DefaultClusterCache<K, V>(id, this.getRedis()));
    }

    @Override
    public <V> IClusterSet<V> getSet(String name) {
        return sets.computeIfAbsent(name, id -> new DefaultClusterSet<V>(id, this.getRedis()));
    }

    @Override
    public IClusterCounter getCounter(String name) {
        return new DefaultClusterCounter(stringOperations, clusterName + ":counter:" + name);
    }
}
