package com.sipa.boot.java8.iot.core.config;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.sipa.boot.java8.iot.core.base.IValue;
import com.sipa.boot.java8.iot.core.base.IValues;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterCache;
import com.sipa.boot.java8.iot.core.config.base.IConfigStorage;

import reactor.core.publisher.Mono;

public class ClusterConfigStorage implements IConfigStorage {
    IClusterCache<String, Object> cache;

    public ClusterConfigStorage(IClusterCache<String, Object> cache) {
        this.cache = cache;
    }

    public IClusterCache<String, Object> getCache() {
        return cache;
    }

    @Override
    public Mono<IValue> getConfig(String key) {
        if (StringUtils.isEmpty(key)) {
            return Mono.empty();
        }
        return cache.get(key).map(IValue::simple);
    }

    @Override
    public Mono<IValues> getConfigs(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return Mono.empty();
        }
        return cache.get(keys)
            .collectList()
            .map(list -> list.stream()
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (_1, _2) -> _2)))
            .map(IValues::of);
    }

    @Override
    public Mono<Boolean> setConfigs(Map<String, Object> values) {
        if (CollectionUtils.isEmpty(values)) {
            return Mono.just(true);
        }
        return cache.putAll(values);
    }

    @Override
    public Mono<Boolean> setConfig(String key, Object value) {
        if (key == null) {
            return Mono.just(true);
        }
        return cache.put(key, value);
    }

    @Override
    public Mono<IValue> getAndRemove(String key) {
        return cache.getAndRemove(key).map(IValue::simple);
    }

    @Override
    public Mono<Boolean> remove(String key) {
        return cache.remove(key);
    }

    @Override
    public Mono<Boolean> remove(Collection<String> key) {
        return cache.remove(key);
    }

    @Override
    public Mono<Boolean> clear() {
        return cache.clear().thenReturn(true);
    }

    @Override
    public Mono<Void> refresh(Collection<String> keys) {
        return cache.refresh(keys);
    }

    @Override
    public Mono<Void> refresh() {
        return cache.refresh();
    }
}
