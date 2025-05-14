package com.sipa.boot.java8.data.iotdb.convert.base;

import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

import com.aliyun.hitsdb.client.value.request.Point;

/**
 * tsdb point data type convert.
 *
 * @author zhouxiajie
 * @date 2021/5/28
 */
public interface IIotdbDataTypeConverter {
    /**
     * convert tsdb point to iotdb data type.
     *
     * @param value
     *            tsdb value
     * @return iotdb data type
     */
    TSDataType covert(Object value);

    /**
     * convert tsdb point to iotdb data type.
     *
     * @param point
     *            tsdb point
     * @return iotdb data type
     */
    TSDataType covert(Point point);
}
