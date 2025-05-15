package com.sipa.boot.java8.iot.core.metadata.base;

import org.springframework.data.redis.connection.DataType;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IEventMetadata extends IMetadata, IJsonable {
    DataType getType();

    default IEventMetadata merge(IEventMetadata another, IMergeOption... option) {
        throw new UnsupportedOperationException("不支持事件物模型合并");
    }
}
