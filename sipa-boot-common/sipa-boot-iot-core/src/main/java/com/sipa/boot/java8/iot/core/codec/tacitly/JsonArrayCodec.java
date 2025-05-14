package com.sipa.boot.java8.iot.core.codec.tacitly;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.alibaba.fastjson.JSON;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class JsonArrayCodec<T, R> implements ICodec<R> {
    private final Class<T> type;

    private final Class<R> resultType;

    private final Function<List<T>, R> mapper;

    private JsonArrayCodec(Class<T> type, Class<R> resultType, Function<List<T>, R> mapper) {
        this.type = type;
        this.resultType = resultType;
        this.mapper = mapper;
    }

    public static <T> JsonArrayCodec<T, List<T>> of(Class<T> type) {
        return JsonArrayCodec.of(type, (Class)List.class, Function.identity());
    }

    public static <T, R> JsonArrayCodec<T, R> of(Class<T> type, Class<R> resultType, Function<List<T>, R> function) {
        return new JsonArrayCodec<>(type, resultType, function);
    }

    @Override
    public Class<R> forType() {
        return resultType;
    }

    @Override
    public R decode(@Nonnull IPayload payload) {
        return mapper.apply(JSON.parseArray(payload.bodyToString(false), type));
    }

    @Override
    public IPayload encode(R body) {
        return IPayload.of(JSON.toJSONBytes(body));
    }
}
