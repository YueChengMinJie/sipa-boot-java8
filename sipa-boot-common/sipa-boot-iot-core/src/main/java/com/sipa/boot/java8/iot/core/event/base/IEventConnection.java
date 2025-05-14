package com.sipa.boot.java8.iot.core.event.base;

import com.sipa.boot.java8.iot.core.dict.base.IEnumDict;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

/**
 * 事件连接
 *
 * @author caszhou
 * @date 2021/10/3
 */
public interface IEventConnection extends Disposable {
    String getId();

    boolean isAlive();

    void doOnDispose(Disposable disposable);

    IEventBroker getBroker();

    default EFeature[] features() {
        return new EFeature[0];
    }

    /**
     * @return 是否为事件生产者
     */
    default boolean isProducer() {
        return this instanceof IEventProducer;
    }

    /**
     * @return 是否为事件消费者
     */
    default boolean isConsumer() {
        return this instanceof IEventConsumer;
    }

    /**
     * @return 转为事件生产者
     */
    default Mono<IEventProducer> asProducer() {
        return isProducer() ? Mono.just(this).cast(IEventProducer.class) : Mono.empty();
    }

    /**
     * @return 转为事件消费者
     */
    default Mono<IEventConsumer> asConsumer() {
        return isConsumer() ? Mono.just(this).cast(IEventConsumer.class) : Mono.empty();
    }

    enum EFeature implements IEnumDict<String> {
        // 消费同一个broker的消息
        consumeSameBroker,
        // 消费同一个connection的消息
        consumeSameConnection,
        // 订阅其他broker的消息
        consumeAnotherBroker,;

        @Override
        public String getValue() {
            return name();
        }

        @Override
        public String getText() {
            return name();
        }
    }
}
