package com.sipa.boot.java8.iot.core.protocol.management;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sipa.boot.java8.iot.core.cluster.base.IClusterCache;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterManager;
import com.sipa.boot.java8.iot.core.protocol.management.base.IProtocolSupportManager;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
@Component
public class DefaultProtocolSupportManager implements IProtocolSupportManager {
    private final IClusterManager clusterManager;

    private final IClusterCache<String, ProtocolSupportDefinition> cache;

    public DefaultProtocolSupportManager(IClusterManager clusterManager) {
        this.clusterManager = clusterManager;
        this.cache = clusterManager.getCache("__protocol_supports");
    }

    @Override
    public Mono<Boolean> store(Flux<ProtocolSupportDefinition> all) {
        return all.collect(Collectors.toMap(ProtocolSupportDefinition::getId, Function.identity()))
            .flatMap(cache::putAll);
    }

    @Override
    public Flux<ProtocolSupportDefinition> loadAll() {
        return cache.values();
    }

    @Override
    public Mono<Boolean> save(ProtocolSupportDefinition definition) {
        return cache.put(definition.getId(), definition)
            .flatMap(su -> clusterManager.getTopic("_protocol_changed").publish(Mono.just(definition)).thenReturn(su));
    }

    @Override
    public Mono<Boolean> remove(String id) {
        return cache.get(id).doOnNext(def -> def.setState((byte)-1)).flatMap(this::save).then(cache.remove(id));
    }
}
