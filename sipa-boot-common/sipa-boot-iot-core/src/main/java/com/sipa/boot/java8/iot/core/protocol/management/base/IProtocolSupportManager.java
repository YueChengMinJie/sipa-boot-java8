package com.sipa.boot.java8.iot.core.protocol.management.base;

import com.sipa.boot.java8.iot.core.protocol.management.ProtocolSupportDefinition;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public interface IProtocolSupportManager {
    Mono<Boolean> store(Flux<ProtocolSupportDefinition> all);

    Flux<ProtocolSupportDefinition> loadAll();

    Mono<Boolean> save(ProtocolSupportDefinition definition);

    Mono<Boolean> remove(String id);
}
