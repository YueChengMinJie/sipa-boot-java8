package com.sipa.boot.java8.iot.core.metadata.base;

import java.io.Serializable;

import org.springframework.data.redis.connection.DataType;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IConfigPropertyMetadata extends IConfigScopeSupport, Serializable {
    String getProperty();

    String getName();

    String getDescription();

    DataType getType();
}
