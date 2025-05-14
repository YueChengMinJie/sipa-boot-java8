package com.sipa.boot.java8.iot.core.codec.base;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public interface IDecoder<T> {
    Class<T> forType();

    T decode(@Nonnull IPayload payload);

    default boolean isDecodeFrom(Object nativeObject) {
        if (nativeObject == null) {
            return false;
        }
        return forType().isInstance(nativeObject);
    }
}
