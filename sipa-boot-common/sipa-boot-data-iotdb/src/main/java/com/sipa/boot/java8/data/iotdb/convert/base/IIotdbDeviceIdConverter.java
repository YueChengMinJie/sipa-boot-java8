package com.sipa.boot.java8.data.iotdb.convert.base;

import com.aliyun.hitsdb.client.value.request.Point;

/**
 * tsdb point device id convert.
 *
 * @author zhouxiajie
 * @date 2021/5/28
 */
public interface IIotdbDeviceIdConverter {
    /**
     * convert tsdb point to iotdb device id.
     *
     * @param point
     *            tsdb point
     * @return iotdb device id
     */
    String covert(Point point);
}
