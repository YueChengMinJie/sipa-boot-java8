package com.sipa.boot.java8.iot.core.message.codec;

import java.nio.charset.StandardCharsets;

import com.sipa.boot.java8.iot.core.enumerate.EMessagePayloadType;
import com.sipa.boot.java8.iot.core.message.codec.base.IEncodedMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class SimpleEncodedMessage implements IEncodedMessage {
    private final ByteBuf payload;

    private final EMessagePayloadType payloadType;

    public SimpleEncodedMessage(ByteBuf payload, EMessagePayloadType payloadType) {
        this.payload = payload;
        this.payloadType = payloadType;
    }

    public static SimpleEncodedMessage of(ByteBuf byteBuf, EMessagePayloadType payloadType) {
        return new SimpleEncodedMessage(byteBuf, payloadType);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (ByteBufUtil.isText(payload, StandardCharsets.UTF_8)) {
            builder.append(payload.toString(StandardCharsets.UTF_8));
        } else {
            ByteBufUtil.appendPrettyHexDump(builder, payload);
        }
        return builder.toString();
    }

    @Override
    public ByteBuf getPayload() {
        return payload;
    }

    @Override
    public EMessagePayloadType getPayloadType() {
        return payloadType;
    }
}
