package com.sipa.boot.java8.iot.core.config.base;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public interface IConfigKeyValue<V> extends IConfigKey<V> {
    V getValue();

    default boolean isNull() {
        return null == getValue();
    }

    default boolean isNotNull() {
        return null != getValue();
    }
}
