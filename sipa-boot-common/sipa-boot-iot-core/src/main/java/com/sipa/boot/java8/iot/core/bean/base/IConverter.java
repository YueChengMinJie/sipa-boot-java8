package com.sipa.boot.java8.iot.core.bean.base;

/**
 * @author caszhou
 * @date 2021/9/24
 */
@FunctionalInterface
public interface IConverter {
    <T> T convert(Object source, Class<T> targetClass, Class<?>[] genericType);
}
