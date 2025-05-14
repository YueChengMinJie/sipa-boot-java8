package com.sipa.boot.java8.iot.core.config.base;

import com.sipa.boot.java8.iot.core.config.SimpleConfigKey;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public interface IConfigKey<V> {
    String getKey();

    default String getName() {
        return getKey();
    }

    default Class<V> getType() {
        return (Class<V>)Object.class;
    }

    static <T> IConfigKey<T> of(String key) {
        return of(key, key);
    }

    static <T> IConfigKey<T> of(String key, String name) {
        return new SimpleConfigKey(key, name, Object.class);
    }

    static <T> IConfigKey<T> of(String key, String name, Class<T> type) {
        return new SimpleConfigKey(key, name, type);
    }

    default IConfigKeyValue<V> value(V value) {
        return new IConfigKeyValue<V>() {
            @Override
            public V getValue() {
                return value;
            }

            @Override
            public String getKey() {
                return IConfigKey.this.getKey();
            }

            @Override
            public String getName() {
                return IConfigKey.this.getName();
            }

            @Override
            public Class<V> getType() {
                return IConfigKey.this.getType();
            }
        };
    }
}
