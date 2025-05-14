package com.sipa.boot.java8.iot.core.protocol.base;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IProtocolSupports {
    boolean isSupport(String protocol);

    Mono<IProtocolSupport> getProtocol(String protocol);

    Flux<IProtocolSupport> getProtocols();
}
