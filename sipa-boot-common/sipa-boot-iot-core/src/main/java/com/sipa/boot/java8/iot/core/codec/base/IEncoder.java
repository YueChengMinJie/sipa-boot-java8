package com.sipa.boot.java8.iot.core.codec.base;

import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public interface IEncoder<T> {
    IPayload encode(T body);
}
