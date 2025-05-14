package com.sipa.boot.java8.iot.core.base;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import com.sipa.boot.java8.iot.core.SimpleValues;
import com.sipa.boot.java8.iot.core.config.base.IConfigKey;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IValues {
    Map<String, Object> getAllValues();

    Optional<IValue> getValue(String key);

    IValues merge(IValues source);

    int size();

    Set<String> getNonExistentKeys(Collection<String> keys);

    default boolean isEmpty() {
        return size() == 0;
    }

    default boolean isNoEmpty() {
        return size() > 0;
    }

    default <T> Optional<T> getValue(IConfigKey<T> key) {
        return getValue(key.getKey()).map(val -> (val.as(key.getType())));
    }

    default String getString(String key, Supplier<String> defaultValue) {
        return getValue(key).map(IValue::asString).orElseGet(defaultValue);
    }

    default String getString(String key, String defaultValue) {
        return getString(key, () -> defaultValue);
    }

    default Number getNumber(String key, Supplier<Number> defaultValue) {
        return getValue(key).map(IValue::asNumber).orElseGet(defaultValue);
    }

    default Number getNumber(String key, Number defaultValue) {
        return getNumber(key, () -> defaultValue);
    }

    static IValues of(Map<String, ?> values) {
        return new SimpleValues((Map)values);
    }
}
