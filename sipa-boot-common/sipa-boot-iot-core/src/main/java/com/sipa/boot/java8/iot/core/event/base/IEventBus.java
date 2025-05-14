package com.sipa.boot.java8.iot.core.event.base;

import org.reactivestreams.Publisher;

import com.sipa.boot.java8.iot.core.codec.Codecs;
import com.sipa.boot.java8.iot.core.codec.base.IDecoder;
import com.sipa.boot.java8.iot.core.codec.base.IEncoder;
import com.sipa.boot.java8.iot.core.codec.tacitly.DirectCodec;
import com.sipa.boot.java8.iot.core.event.Subscription;
import com.sipa.boot.java8.iot.core.event.TopicPayload;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public interface IEventBus {
    Flux<TopicPayload> subscribe(Subscription subscription);

    <T> Flux<T> subscribe(Subscription subscription, IDecoder<T> decoder);

    default <T> Flux<T> subscribe(Subscription subscription, Class<T> type) {
        return subscribe(subscription, Codecs.lookup(type));
    }

    <T> Mono<Long> publish(String topic, Publisher<T> event);

    <T> Mono<Long> publish(String topic, IEncoder<T> encoder, Publisher<? extends T> eventStream);

    <T> Mono<Long> publish(String topic, IEncoder<T> encoder, Publisher<? extends T> eventStream, Scheduler scheduler);

    default <T> Mono<Long> publish(String topic, IEncoder<T> encoder, T event) {
        return publish(topic, encoder, Mono.just(event));
    }

    default <T> Mono<Long> publish(String topic, IEncoder<T> encoder, T event, Scheduler scheduler) {
        return publish(topic, encoder, Mono.just(event), scheduler);
    }

    default <T> Mono<Long> publish(String topic, T event) {
        if (event instanceof IPayload) {
            return publish(topic, ((IPayload)event));
        }
        return publish(topic, Codecs.lookup(event.getClass()), event);
    }

    default <T> Mono<Long> publish(String topic, T event, Scheduler scheduler) {
        if (event instanceof IPayload) {
            return publish(topic, ((IPayload)event), scheduler);
        }
        return publish(topic, Codecs.lookup(event.getClass()), event, scheduler);
    }

    default Mono<Long> publish(String topic, IPayload event) {
        return publish(topic, DirectCodec.INSTANCE, event);
    }

    default Mono<Long> publish(String topic, IPayload event, Scheduler scheduler) {
        return publish(topic, DirectCodec.INSTANCE, event, scheduler);
    }
}
