package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.common.utils.BytesUtils;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class IntegerCodec implements ICodec<Integer> {
    public static IntegerCodec INSTANCE = new IntegerCodec();

    private IntegerCodec() {
    }

    @Override
    public Class<Integer> forType() {
        return Integer.class;
    }

    @Override
    public Integer decode(@Nonnull IPayload payload) {
        return BytesUtils.beToInt(payload.getBytes(false));
    }

    @Override
    public IPayload encode(Integer body) {
        return IPayload.of(BytesUtils.intToBe(body));
    }
}
