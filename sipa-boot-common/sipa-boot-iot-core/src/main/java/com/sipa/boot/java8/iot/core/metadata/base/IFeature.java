package com.sipa.boot.java8.iot.core.metadata.base;

import com.sipa.boot.java8.iot.core.metadata.SimpleFeature;

/**
 * 特性接口,一般使用枚举实现。用于定义协议或者设备支持的某些特性.
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IFeature {
    /**
     * @return 唯一标识
     */
    String getId();

    /**
     * @return 名称
     */
    String getName();

    static IFeature of(String id, String name) {
        return new SimpleFeature(id, name);
    }
}
