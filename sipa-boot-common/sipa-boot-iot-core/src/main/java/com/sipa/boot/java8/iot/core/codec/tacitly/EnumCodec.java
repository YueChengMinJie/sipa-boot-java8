package com.sipa.boot.java8.iot.core.codec.tacitly;

import java.util.Arrays;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class EnumCodec<T extends Enum<?>> implements ICodec<T> {
    private final T[] values;

    public EnumCodec(T[] values) {
        this.values = values;
    }

    @Override
    public Class<T> forType() {
        return (Class<T>)values[0].getDeclaringClass();
    }

    @Override
    public T decode(@Nonnull IPayload payload) {
        byte[] bytes = payload.getBytes(false);

        if (bytes.length > 0 && bytes[0] <= values.length) {
            return values[bytes[0] & 0xFF];
        }
        throw new IllegalArgumentException(
            "can not decode payload " + Arrays.toString(bytes) + " to enums " + Arrays.toString(values));
    }

    @Override
    public IPayload encode(T body) {
        return IPayload.of(new byte[] {(byte)body.ordinal()});
    }
}
