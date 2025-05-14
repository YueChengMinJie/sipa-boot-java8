package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

import io.netty.buffer.ByteBuf;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class ByteBufCodec implements ICodec<ByteBuf> {
    public static final ByteBufCodec INSTANCE = new ByteBufCodec();

    @Override
    public Class<ByteBuf> forType() {
        return ByteBuf.class;
    }

    @Override
    public ByteBuf decode(@Nonnull IPayload payload) {
        return payload.getBody();
    }

    @Override
    public IPayload encode(ByteBuf body) {
        return IPayload.of(body);
    }
}
