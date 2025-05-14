package com.sipa.boot.java8.iot.core.event.base;

import com.sipa.boot.java8.iot.core.event.Subscription;
import com.sipa.boot.java8.iot.core.event.TopicPayload;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 事件生产者
 *
 * @author caszhou
 * @date 2021/10/3
 */
public interface IEventProducer extends IEventConnection {
    /**
     * 发送订阅请求
     *
     * @param subscription
     *            订阅请求
     */
    Mono<Void> subscribe(Subscription subscription);

    /**
     * 发送取消订阅请求
     *
     * @param subscription
     *            订阅请求
     */
    Mono<Void> unsubscribe(Subscription subscription);

    /**
     * 从生产者订阅消息
     *
     * @return 消息流
     */
    Flux<TopicPayload> subscribe();
}
