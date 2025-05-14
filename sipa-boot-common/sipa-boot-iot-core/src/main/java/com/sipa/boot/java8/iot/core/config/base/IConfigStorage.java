package com.sipa.boot.java8.iot.core.config.base;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.sipa.boot.java8.iot.core.base.IValue;
import com.sipa.boot.java8.iot.core.base.IValues;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public interface IConfigStorage {
    Mono<IValue> getConfig(String key);

    default Mono<IValues> getConfigs(String... key) {
        return getConfigs(Arrays.asList(key));
    }

    Mono<IValues> getConfigs(Collection<String> key);

    Mono<Boolean> setConfigs(Map<String, Object> values);

    Mono<Boolean> setConfig(String key, Object value);

    Mono<Boolean> remove(String key);

    Mono<IValue> getAndRemove(String key);

    Mono<Boolean> remove(Collection<String> key);

    Mono<Boolean> clear();

    default Mono<Void> refresh(Collection<String> keys) {
        return Mono.empty();
    }

    default Mono<Void> refresh() {
        return Mono.empty();
    }
}
