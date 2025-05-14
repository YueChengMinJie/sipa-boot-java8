package com.sipa.boot.java8.iot.core.base;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.sipa.boot.java8.iot.core.config.base.IConfigKey;
import com.sipa.boot.java8.iot.core.config.base.IConfigKeyValue;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IConfigurable {
    /**
     * 获取配置,如果值不存在则返回{@link Mono#empty()}
     *
     * @param key
     *            key
     * @return 结果包装器, 不会为null
     */
    Mono<IValue> getConfig(String key);

    /**
     * 获取多个配置信息
     *
     * @param keys
     *            配置key集合
     * @return 配置信息
     */
    Mono<IValues> getConfigs(Collection<String> keys);

    /**
     * 设置一个配置,配置最好以基本数据类型或者json为主
     *
     * @param key
     *            配置key
     * @param value
     *            值 不能为null
     */
    Mono<Boolean> setConfig(String key, Object value);

    default Mono<Boolean> setConfig(IConfigKeyValue<?> keyValue) {
        return setConfig(keyValue.getKey(), keyValue.getValue());
    }

    default <T> Mono<Boolean> setConfig(IConfigKey<T> key, T value) {
        return setConfig(key.getKey(), value);
    }

    default Mono<Boolean> setConfigs(IConfigKeyValue<?>... keyValues) {
        return setConfigs(Arrays.stream(keyValues)
            .filter(IConfigKeyValue::isNotNull)
            .collect(Collectors.toMap(IConfigKeyValue::getKey, IConfigKeyValue::getValue)));
    }

    default <V> Mono<V> getConfig(IConfigKey<V> key) {
        return getConfig(key.getKey()).flatMap(value -> Mono.justOrEmpty(value.as(key.getType())));
    }

    default Mono<IValues> getConfigs(IConfigKey<?>... key) {
        return getConfigs(Arrays.stream(key).map(IConfigKey::getKey).collect(Collectors.toSet()));
    }

    /**
     * 获取多个配置,如果未指定key,则获取全部配置
     *
     * @return 所有配置结果集合
     */
    default Mono<IValues> getConfigs(String... keys) {
        return getConfigs(Arrays.asList(keys));
    }

    /**
     * 批量设置配置
     *
     * @param conf
     *            配置内容
     */
    Mono<Boolean> setConfigs(Map<String, Object> conf);

    /**
     * 删除配置
     *
     * @param key
     *            key
     */
    Mono<Boolean> removeConfig(String key);

    /**
     * 获取并删除配置
     *
     * @param key
     *            key
     * @return 被删除的配置
     * @since 1.1.1
     */
    Mono<IValue> getAndRemoveConfig(String key);

    /**
     * 删除配置
     *
     * @param key
     *            key
     * @return 被删除的值，不存在则返回empty
     */
    Mono<Boolean> removeConfigs(Collection<String> key);

    /**
     * 刷新配置信息
     *
     * @return key
     */
    Mono<Void> refreshConfig(Collection<String> keys);

    /**
     * 刷新全部配置信息
     *
     * @return key
     */
    Mono<Void> refreshAllConfig();

    /**
     * 删除多个配置信息
     *
     * @param key
     *            key
     * @return 删除结果
     */
    default Mono<Boolean> removeConfigs(IConfigKey<?>... key) {
        return removeConfigs(Arrays.stream(key).map(IConfigKey::getKey).collect(Collectors.toSet()));
    }
}
