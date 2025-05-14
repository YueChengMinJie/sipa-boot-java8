package com.sipa.boot.java8.data.iotdb.convert.impl;

import java.util.Objects;

import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.springframework.stereotype.Component;

import com.aliyun.hitsdb.client.value.request.Point;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.data.iotdb.convert.base.IIotdbDataTypeConverter;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
@Component("defaultDataTypeConverter")
public class IotdbDataTypeConverterImpl implements IIotdbDataTypeConverter {
    private static final Log LOGGER = LogFactory.get(IotdbDataTypeConverterImpl.class);

    @Override
    public TSDataType covert(Object value) {
        return doConvert(value);
    }

    @Override
    public TSDataType covert(Point point) {
        if (Objects.nonNull(point)) {
            Object value = point.getValue();
            return doConvert(value);
        } else {
            LOGGER.warn("Point is null.");
        }
        return null;
    }

    private TSDataType doConvert(Object value) {
        if (Objects.nonNull(value)) {
            return getTsDataType(value);
        } else {
            LOGGER.warn("Value is null.");
            return null;
        }
    }

    private TSDataType getTsDataType(Object value) {
        if (Long.class.equals(value.getClass())) {
            return TSDataType.INT64;
        } else if (Integer.class.equals(value.getClass())) {
            return TSDataType.INT32;
        } else if (Float.class.equals(value.getClass())) {
            return TSDataType.FLOAT;
        } else if (Double.class.equals(value.getClass())) {
            return TSDataType.DOUBLE;
        } else if (String.class.equals(value.getClass())) {
            return TSDataType.TEXT;
        } else if (Boolean.class.equals(value.getClass())) {
            return TSDataType.BOOLEAN;
        } else {
            LOGGER.warn("Value class is [{}]", value.getClass().getName());
            return null;
        }
    }
}
