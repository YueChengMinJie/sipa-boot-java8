package com.sipa.boot.java8.iot.core.message.codec.base;

import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.enumerate.EMessagePayloadType;
import com.sipa.boot.java8.iot.core.message.codec.EmptyMessage;
import com.sipa.boot.java8.iot.core.message.codec.SimpleEncodedMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public interface IEncodedMessage {
    ByteBuf getPayload();

    default String payloadAsString() {
        return getPayload().toString(StandardCharsets.UTF_8);
    }

    default JSONObject payloadAsJson() {
        return (JSONObject)JSON.parse(payloadAsBytes());
    }

    default JSONArray payloadAsJsonArray() {
        return (JSONArray)JSON.parse(payloadAsBytes());
    }

    default byte[] payloadAsBytes() {
        return ByteBufUtil.getBytes(getPayload());
    }

    @Deprecated
    default byte[] getBytes() {
        return ByteBufUtil.getBytes(getPayload());
    }

    default byte[] getBytes(int offset, int len) {
        return ByteBufUtil.getBytes(getPayload(), offset, len);
    }

    @Deprecated
    default EMessagePayloadType getPayloadType() {
        return EMessagePayloadType.JSON;
    }

    static EmptyMessage empty() {
        return EmptyMessage.INSTANCE;
    }

    static IEncodedMessage simple(ByteBuf data) {
        return simple(data, EMessagePayloadType.BINARY);
    }

    static IEncodedMessage simple(ByteBuf data, EMessagePayloadType payloadType) {
        return SimpleEncodedMessage.of(data, payloadType);
    }
}
