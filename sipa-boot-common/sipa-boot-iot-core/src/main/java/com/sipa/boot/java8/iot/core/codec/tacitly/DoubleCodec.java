package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.common.utils.BytesUtils;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class DoubleCodec implements ICodec<Double> {
    public static DoubleCodec INSTANCE = new DoubleCodec();

    private DoubleCodec() {
    }

    @Override
    public Class<Double> forType() {
        return Double.class;
    }

    @Override
    public Double decode(@Nonnull IPayload payload) {
        return BytesUtils.beToDouble(payload.getBytes(false));
    }

    @Override
    public IPayload encode(Double body) {
        return IPayload.of(BytesUtils.doubleToBe(body));
    }
}
