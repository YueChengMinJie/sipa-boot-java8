package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.alibaba.fastjson.JSON;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;
import com.sipa.boot.java8.iot.core.metadata.base.IJsonable;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class JsonCodec<T> implements ICodec<T> {
    private final Class<? extends T> type;

    private JsonCodec(Class<? extends T> type) {
        this.type = type;
    }

    public static <T> JsonCodec<T> of(Class<? extends T> type) {
        return new JsonCodec<>(type);
    }

    @Override
    public Class<T> forType() {
        return (Class<T>)type;
    }

    @Override
    public T decode(@Nonnull IPayload payload) {
        return JSON.parseObject(payload.getBytes(false), type);
    }

    @Override
    public IPayload encode(T body) {
        if (body instanceof IJsonable) {
            return IPayload.of(JSON.toJSONBytes(((IJsonable)body).toJson()));
        }
        return IPayload.of(JSON.toJSONBytes(body));
    }
}
