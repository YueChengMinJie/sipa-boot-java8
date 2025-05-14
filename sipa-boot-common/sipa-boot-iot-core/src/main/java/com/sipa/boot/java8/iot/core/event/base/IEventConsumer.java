package com.sipa.boot.java8.iot.core.event.base;

import com.sipa.boot.java8.iot.core.event.Subscription;
import com.sipa.boot.java8.iot.core.event.TopicPayload;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * 事件消费者
 *
 * @author caszhou
 * @date 2021/10/3
 */
public interface IEventConsumer extends IEventConnection {
    Flux<Subscription> handleSubscribe();

    Flux<Subscription> handleUnSubscribe();

    FluxSink<TopicPayload> sink();
}
