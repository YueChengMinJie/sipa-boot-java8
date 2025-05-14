package com.sipa.boot.java8.iot.core.codec.tacitly;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.common.utils.StringUtils;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

import reactor.function.Function3;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class ErrorCodec implements ICodec<Throwable> {
    public static ErrorCodec RUNTIME =
        of((type, msg, stack) -> stack == null ? new RuntimeException(msg) : new RuntimeException(stack));

    public static ErrorCodec DEFAULT = RUNTIME;

    private final Function3</* 异常类型 */String, /* message */ String, /* stack */String, Throwable> mapping;

    private ErrorCodec(Function3<String, String, String, Throwable> mapping) {
        this.mapping = mapping;
    }

    public static ErrorCodec of(Function<String, Throwable> mapping) {
        return new ErrorCodec((type, msg, stack) -> mapping.apply(msg));
    }

    public static ErrorCodec of(Function3<String, String, String, Throwable> mapping) {
        return new ErrorCodec(mapping);
    }

    @Override
    public Class<Throwable> forType() {
        return Throwable.class;
    }

    @Override
    public Throwable decode(@Nonnull IPayload payload) {
        String body = payload.bodyToString(false);
        if (body.startsWith("{")) {
            JSONObject json = JSON.parseObject(body);
            return mapping.apply(json.getString("t"), json.getString("m"), json.getString("s"));
        }
        return mapping.apply(null, body, null);
    }

    @Override
    public IPayload encode(Throwable body) {
        JSONObject state = new JSONObject();
        state.put("m", body.getMessage());
        state.put("t", body.getClass().getName());
        state.put("s", StringUtils.throwable2String(body));
        return IPayload.of(state.toJSONString());
    }
}
