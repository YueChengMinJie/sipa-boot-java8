package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.common.utils.BytesUtils;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class FloatCodec implements ICodec<Float> {
    public static FloatCodec INSTANCE = new FloatCodec();

    private FloatCodec() {
    }

    @Override
    public Class<Float> forType() {
        return Float.class;
    }

    @Override
    public Float decode(@Nonnull IPayload payload) {
        return BytesUtils.beToFloat(payload.getBytes(false));
    }

    @Override
    public IPayload encode(Float body) {
        return IPayload.of(BytesUtils.floatToBe(body));
    }
}
