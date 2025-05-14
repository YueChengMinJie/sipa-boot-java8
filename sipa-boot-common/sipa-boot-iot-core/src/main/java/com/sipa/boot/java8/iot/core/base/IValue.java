package com.sipa.boot.java8.iot.core.base;

import java.util.Date;

import com.sipa.boot.java8.iot.core.SimpleValue;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IValue {
    Object get();

    <T> T as(Class<T> type);

    default String asString() {
        return String.valueOf(get());
    }

    default int asInt() {
        return as(Integer.class);
    }

    default long asLong() {
        return as(Long.class);
    }

    default boolean asBoolean() {
        return Boolean.TRUE.equals(get()) || "true".equals(get());
    }

    default Number asNumber() {
        return as(Number.class);
    }

    default Date asDate() {
        return as(Date.class);
    }

    static IValue simple(Object value) {
        return new SimpleValue(value);
    }
}
