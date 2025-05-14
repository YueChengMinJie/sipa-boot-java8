package com.sipa.boot.java8.iot.core.message.property.base;

/**
 * @author caszhou
 * @date 2021/9/23
 */
public interface IProperty {
    String getId();

    long getTimestamp();

    Object getValue();

    String getState();
}
