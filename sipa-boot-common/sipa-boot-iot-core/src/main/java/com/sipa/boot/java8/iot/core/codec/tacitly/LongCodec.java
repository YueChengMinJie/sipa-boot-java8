package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.common.utils.BytesUtils;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class LongCodec implements ICodec<Long> {
    public static LongCodec INSTANCE = new LongCodec();

    private LongCodec() {
    }

    @Override
    public Class<Long> forType() {
        return Long.class;
    }

    @Override
    public Long decode(@Nonnull IPayload payload) {
        return BytesUtils.beToLong(payload.getBytes(false));
    }

    @Override
    public IPayload encode(Long body) {
        return IPayload.of(BytesUtils.longToBe(body));
    }
}
