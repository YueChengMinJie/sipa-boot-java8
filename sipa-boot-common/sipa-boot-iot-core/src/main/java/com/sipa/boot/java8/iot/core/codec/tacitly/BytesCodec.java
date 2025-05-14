package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class BytesCodec implements ICodec<byte[]> {
    public static BytesCodec INSTANCE = new BytesCodec();

    private BytesCodec() {
    }

    @Override
    public Class<byte[]> forType() {
        return byte[].class;
    }

    @Override
    public byte[] decode(@Nonnull IPayload payload) {
        return payload.getBytes(false);
    }

    @Override
    public IPayload encode(byte[] body) {
        return IPayload.of(body);
    }
}
