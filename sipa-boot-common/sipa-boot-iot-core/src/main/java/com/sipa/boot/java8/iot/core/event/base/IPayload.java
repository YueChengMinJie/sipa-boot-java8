package com.sipa.boot.java8.iot.core.event.base;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.codec.Codecs;
import com.sipa.boot.java8.iot.core.codec.base.IDecoder;
import com.sipa.boot.java8.iot.core.codec.base.IEncoder;
import com.sipa.boot.java8.iot.core.event.ByteBufPayload;
import com.sipa.boot.java8.iot.core.event.NativePayload;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public interface IPayload extends ReferenceCounted {
    @Nonnull
    ByteBuf getBody();

    IPayload voidPayload = IPayload.of(Unpooled.EMPTY_BUFFER);

    default IPayload slice() {
        return IPayload.of(getBody().slice());
    }

    default <T> T decode(IDecoder<T> decoder, boolean release) {
        try {
            return decoder.decode(this);
        } finally {
            if (release) {
                ReferenceCountUtil.safeRelease(this);
            }
        }
    }

    default <T> T decode(IDecoder<T> decoder) {
        return decode(decoder, true);
    }

    default <T> T decode(Class<T> decoder) {
        return decode(decoder, true);
    }

    default <T> T decode(Class<T> decoder, boolean release) {
        return decode(Codecs.lookup(decoder), release);
    }

    default Object decode(boolean release) {
        byte[] payload = getBytes(false);
        if ((payload[0] == 123 && payload[payload.length - 1] == 125)
            || (payload[0] == 91 && payload[payload.length - 1] == 93)) {
            try {
                return JSON.parse(new String(payload));
            } finally {
                if (release) {
                    ReferenceCountUtil.safeRelease(this);
                }
            }
        }
        return decode(Object.class, release);
    }

    default Object decode() {
        return decode(true);
    }

    default <T> T convert(Function<ByteBuf, T> mapper) {
        return convert(mapper, true);
    }

    default <T> T convert(Function<ByteBuf, T> mapper, boolean release) {
        ByteBuf body = getBody();
        try {
            return mapper.apply(body);
        } finally {
            if (release) {
                ReferenceCountUtil.safeRelease(this);
            }
        }
    }

    @Override
    default IPayload retain() {
        return retain(1);
    }

    @Override
    default IPayload retain(int inc) {
        getBody().retain(inc);
        return this;
    }

    @Override
    default boolean release(int dec) {
        if (refCnt() >= dec) {
            return ReferenceCountUtil.release(getBody(), dec);
        }
        return true;
    }

    @Override
    default boolean release() {
        return release(1);
    }

    default byte[] getBytes() {
        return getBytes(true);
    }

    default byte[] getBytes(boolean release) {
        return convert(ByteBufUtil::getBytes, release);
    }

    default byte[] getBytes(int offset, int length, boolean release) {
        return convert(byteBuf -> ByteBufUtil.getBytes(byteBuf, offset, length), release);
    }

    default String bodyToString() {
        return bodyToString(true);
    }

    default String bodyToString(boolean release) {
        try {
            return getBody().toString(StandardCharsets.UTF_8);
        } finally {
            if (release) {
                ReferenceCountUtil.safeRelease(this);
            }
        }
    }

    default JSONObject bodyToJson(boolean release) {
        return decode(JSONObject.class, release);
    }

    default JSONObject bodyToJson() {
        return bodyToJson(true);
    }

    default JSONArray bodyToJsonArray() {
        return bodyToJsonArray(true);
    }

    default JSONArray bodyToJsonArray(boolean release) {
        return decode(JSONArray.class);
    }

    @Override
    default int refCnt() {
        return getBody().refCnt();
    }

    @Override
    default IPayload touch() {
        getBody().touch();
        return this;
    }

    @Override
    default IPayload touch(Object o) {
        getBody().touch(o);
        return this;
    }

    static IPayload of(ByteBuf body) {
        return ByteBufPayload.of(body);
    }

    static IPayload of(byte[] body) {
        return of(Unpooled.wrappedBuffer(body));
    }

    static IPayload of(String body) {
        return of(body.getBytes());
    }

    static <T> IPayload of(T body, IEncoder<T> encoder) {
        if (body instanceof IPayload) {
            return encoder.encode(body);
        }
        return NativePayload.of(body, encoder);
    }
}
