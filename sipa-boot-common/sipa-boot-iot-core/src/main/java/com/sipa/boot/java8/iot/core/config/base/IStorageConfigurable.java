package com.sipa.boot.java8.iot.core.config.base;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.sipa.boot.java8.iot.core.base.IConfigurable;
import com.sipa.boot.java8.iot.core.base.IValue;
import com.sipa.boot.java8.iot.core.base.IValues;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public interface IStorageConfigurable extends IConfigurable {
    Mono<IConfigStorage> getReactiveStorage();

    default Mono<? extends IConfigurable> getParent() {
        return Mono.empty();
    }

    @Override
    default Mono<IValue> getConfig(String key) {
        return getConfig(key, true);
    }

    default Mono<IValue> getConfig(String key, boolean fallbackParent) {
        return getReactiveStorage().flatMap(store -> store.getConfig(key))
            .switchIfEmpty(
                Mono.defer(() -> fallbackParent ? getParent().flatMap(parent -> parent.getConfig(key)) : Mono.empty()));
    }

    default Mono<IValues> getConfigs(Collection<String> keys, boolean fallbackParent) {
        return getReactiveStorage().flatMap(store -> store.getConfigs(keys)).flatMap(values -> {
            // 尝试获取上一级的配置
            if (!keys.isEmpty() && values.size() != keys.size() && fallbackParent) {
                Set<String> nonExistent = values.getNonExistentKeys(keys);
                return getParent().flatMap(parent -> parent.getConfigs(nonExistent))
                    .map(parentValues -> parentValues.merge(values))
                    .defaultIfEmpty(values);
            }
            return Mono.just(values);
        });
    }

    @Override
    default Mono<IValues> getConfigs(Collection<String> keys) {
        return getConfigs(keys, true);
    }

    @Override
    default Mono<Boolean> setConfig(String key, Object value) {
        return getReactiveStorage().flatMap(store -> store.setConfig(key, value));
    }

    @Override
    default Mono<Boolean> setConfigs(Map<String, Object> conf) {
        return getReactiveStorage().flatMap(storage -> storage.setConfigs(conf));
    }

    @Override
    default Mono<Boolean> removeConfig(String key) {
        return getReactiveStorage().flatMap(storage -> storage.remove(key));
    }

    @Override
    default Mono<IValue> getAndRemoveConfig(String key) {
        return getReactiveStorage().flatMap(storage -> storage.getAndRemove(key));
    }

    @Override
    default Mono<Boolean> removeConfigs(Collection<String> key) {
        return getReactiveStorage().flatMap(storage -> storage.remove(key));
    }

    @Override
    default Mono<Void> refreshConfig(Collection<String> keys) {
        return getReactiveStorage().flatMap(storage -> storage.refresh(keys));
    }

    @Override
    default Mono<Void> refreshAllConfig() {
        return getReactiveStorage().flatMap(IConfigStorage::refresh);
    }
}
