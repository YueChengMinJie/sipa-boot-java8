package com.sipa.boot.java8.iot.core.metadata.base;

import com.fasterxml.jackson.databind.PropertyMetadata;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public interface IPropertyMetadata extends IMetadata, IJsonable {
    IDataType getValueType();

    default PropertyMetadata merge(PropertyMetadata another, IMergeOption... option) {
        throw new UnsupportedOperationException("不支持属性物模型合并");
    }
}
