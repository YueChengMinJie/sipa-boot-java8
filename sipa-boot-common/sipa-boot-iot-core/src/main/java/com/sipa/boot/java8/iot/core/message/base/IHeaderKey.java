package com.sipa.boot.java8.iot.core.message.base;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public interface IHeaderKey<T> {
    String getKey();

    T getDefaultValue();

    default Class<T> getType() {
        return getDefaultValue() == null ? (Class<T>)Object.class : (Class<T>)getDefaultValue().getClass();
    }

    static <T> IHeaderKey<T> of(String key, T defaultValue, Class<T> type) {
        return new IHeaderKey<T>() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public T getDefaultValue() {
                return defaultValue;
            }

            @Override
            public Class<T> getType() {
                return type;
            }
        };
    }

    static <T> IHeaderKey<T> of(String key, T defaultValue) {
        return of(key, defaultValue, defaultValue == null ? (Class<T>)Object.class : (Class<T>)defaultValue.getClass());
    }
}
