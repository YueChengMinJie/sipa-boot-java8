package com.sipa.boot.java8.iot.core.bean.base;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public interface IBeanFactory {
    <T> T newInstance(Class<T> beanType);
}
