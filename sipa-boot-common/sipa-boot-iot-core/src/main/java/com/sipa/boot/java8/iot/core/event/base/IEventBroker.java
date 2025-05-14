package com.sipa.boot.java8.iot.core.event.base;

import reactor.core.publisher.Flux;

/**
 * 事件代理
 *
 * @author caszhou
 * @date 2021/10/3
 */
public interface IEventBroker {
    String getId();

    Flux<IEventConnection> accept();
}
