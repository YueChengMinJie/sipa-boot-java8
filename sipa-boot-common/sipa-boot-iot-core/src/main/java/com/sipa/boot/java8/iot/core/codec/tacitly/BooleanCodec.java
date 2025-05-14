package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class BooleanCodec implements ICodec<Boolean> {
    public static BooleanCodec INSTANCE = new BooleanCodec();

    private BooleanCodec() {
    }

    @Override
    public Class<Boolean> forType() {
        return Boolean.class;
    }

    @Override
    public Boolean decode(@Nonnull IPayload payload) {
        byte[] data = payload.getBytes(false);

        return data.length > 0 && data[0] > 0;
    }

    @Override
    public IPayload encode(Boolean body) {
        return IPayload.of(new byte[] {body ? (byte)1 : 0});
    }
}
