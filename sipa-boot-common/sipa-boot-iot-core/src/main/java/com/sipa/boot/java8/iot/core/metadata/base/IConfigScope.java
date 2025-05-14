package com.sipa.boot.java8.iot.core.metadata.base;

/**
 * 配置作用域,请使用枚举实现此接口
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IConfigScope {
    String getId();

    default String getName() {
        return getId();
    }

    static IConfigScope of(String id) {
        return () -> id;
    }
}
