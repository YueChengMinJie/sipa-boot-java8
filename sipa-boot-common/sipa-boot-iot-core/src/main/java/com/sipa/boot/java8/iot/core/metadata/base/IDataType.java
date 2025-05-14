package com.sipa.boot.java8.iot.core.metadata.base;

import java.util.Map;

import com.sipa.boot.java8.iot.core.metadata.ValidateResult;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public interface IDataType extends IMetadata, IFormatSupport {
    /**
     * 验证是否合法
     *
     * @param value
     *            值
     * @return ValidateResult
     */
    ValidateResult validate(Object value);

    /**
     * @return 类型标识
     */
    default String getType() {
        return getId();
    }

    /**
     * @return 拓展属性
     */
    @Override
    default Map<String, Object> getExpands() {
        return null;
    }
}
