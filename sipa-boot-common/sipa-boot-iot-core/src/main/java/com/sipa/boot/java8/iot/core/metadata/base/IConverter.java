package com.sipa.boot.java8.iot.core.metadata.base;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public interface IConverter<T> {
    T convert(Object value);
}
