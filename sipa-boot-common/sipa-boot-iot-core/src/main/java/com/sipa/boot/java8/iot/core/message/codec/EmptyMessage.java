package com.sipa.boot.java8.iot.core.message.codec;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.iot.core.message.codec.base.IEncodedMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public final class EmptyMessage implements IEncodedMessage {
    public static final EmptyMessage INSTANCE = new EmptyMessage();

    private EmptyMessage() {}

    @Nonnull
    @Override
    public ByteBuf getPayload() {
        return Unpooled.wrappedBuffer(new byte[0]);
    }

    @Override
    public String toString() {
        return "empty message";
    }
}
