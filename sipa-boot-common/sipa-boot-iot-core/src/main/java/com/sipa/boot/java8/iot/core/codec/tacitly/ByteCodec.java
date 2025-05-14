package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

import io.netty.buffer.ByteBuf;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class ByteCodec implements ICodec<Byte> {
    public static ByteCodec INSTANCE = new ByteCodec();

    private ByteCodec() {
    }

    @Override
    public Class<Byte> forType() {
        return Byte.class;
    }

    @Override
    public Byte decode(@Nonnull IPayload payload) {
        ByteBuf buf = payload.getBody();
        byte val = buf.getByte(0);
        buf.resetReaderIndex();
        return val;
    }

    @Override
    public IPayload encode(Byte body) {
        return IPayload.of(new byte[] {body});
    }
}
