package com.sipa.boot.java8.iot.core.cluster.base;

import java.util.Collection;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public interface IClusterSet<T> {
    Mono<Boolean> add(T value);

    Mono<Boolean> add(Collection<T> values);

    Mono<Boolean> remove(T value);

    Mono<Boolean> remove(Collection<T> values);

    Flux<T> values();
}
