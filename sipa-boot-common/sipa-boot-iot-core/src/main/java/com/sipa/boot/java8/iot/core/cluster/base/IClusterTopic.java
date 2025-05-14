package com.sipa.boot.java8.iot.core.cluster.base;

import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 集群广播,用于向集群中发送广播消息
 *
 * @param <T>
 * @author caszhou
 * @date 2021/10/2
 */
public interface IClusterTopic<T> {
    /**
     * 按通配符进行订阅,通配符支持 *, 如: message/*
     *
     * @return 消息流
     */
    Flux<ITopicMessage<T>> subscribePattern();

    /**
     * 发送广播消息
     *
     * @param publisher
     *            消息流
     * @return 接收到消息到订阅者数量
     */
    Mono<Integer> publish(Publisher<? extends T> publisher);

    /**
     * 订阅消息
     *
     * @return 消息流
     */
    default Flux<T> subscribe() {
        return subscribePattern().map(ITopicMessage::getMessage);
    }

    interface ITopicMessage<T> {
        String getTopic();

        T getMessage();
    }
}
