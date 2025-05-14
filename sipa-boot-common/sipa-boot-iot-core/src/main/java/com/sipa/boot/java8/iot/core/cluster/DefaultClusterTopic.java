package com.sipa.boot.java8.iot.core.cluster;

import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Publisher;
import org.springframework.data.redis.core.ReactiveRedisOperations;

import com.sipa.boot.java8.iot.core.cluster.base.IClusterTopic;

import reactor.core.Disposable;
import reactor.core.publisher.*;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultClusterTopic<T> implements IClusterTopic<T> {
    private final String topicName;

    private final ReactiveRedisOperations<Object, T> operations;

    private final FluxProcessor<ITopicMessage<T>, ITopicMessage<T>> processor;

    private final FluxSink<ITopicMessage<T>> sink;

    private final AtomicBoolean subscribed = new AtomicBoolean();

    private Disposable disposable;

    public DefaultClusterTopic(String topic, ReactiveRedisOperations<Object, T> operations) {
        this.topicName = topic;
        this.operations = operations;
        processor = EmitterProcessor.create(false);
        sink = processor.sink(FluxSink.OverflowStrategy.BUFFER);
    }

    private void doSubscribe() {
        if (subscribed.compareAndSet(false, true)) {
            disposable = operations.listenToPattern(topicName).subscribe(data -> {
                if (!processor.hasDownstreams()) {
                    disposable.dispose();
                    subscribed.compareAndSet(true, false);
                } else {
                    sink.next(new ITopicMessage<T>() {
                        @Override
                        public String getTopic() {
                            return data.getChannel();
                        }

                        @Override
                        public T getMessage() {
                            return data.getMessage();
                        }
                    });
                }
            });
        }
    }

    @Override
    public Flux<ITopicMessage<T>> subscribePattern() {
        return processor.doOnSubscribe((r) -> doSubscribe());
    }

    @Override
    public Mono<Integer> publish(Publisher<? extends T> publisher) {
        return Flux.from(publisher)
            .flatMap(data -> operations.convertAndSend(topicName, data))
            .last(1L)
            .map(Number::intValue);
    }
}
