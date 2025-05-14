package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class VoidCodec implements ICodec<Void> {
    public static VoidCodec INSTANCE = new VoidCodec();

    @Override
    public Class<Void> forType() {
        return Void.class;
    }

    @Override
    public Void decode(@Nonnull IPayload payload) {
        return null;
    }

    @Override
    public IPayload encode(Void body) {
        return IPayload.of(new byte[0]);
    }

    @Override
    public boolean isDecodeFrom(Object nativeObject) {
        return nativeObject == null;
    }
}
