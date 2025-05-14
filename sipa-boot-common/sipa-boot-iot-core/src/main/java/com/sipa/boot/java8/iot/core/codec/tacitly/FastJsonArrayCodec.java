package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class FastJsonArrayCodec implements ICodec<JSONArray> {
    public static final FastJsonArrayCodec INSTANCE = new FastJsonArrayCodec();

    @Override
    public Class<JSONArray> forType() {
        return JSONArray.class;
    }

    @Override
    public JSONArray decode(@Nonnull IPayload payload) {
        return JSON.parseArray(payload.bodyToString(false));
    }

    @Override
    public IPayload encode(JSONArray body) {
        return IPayload.of(JSON.toJSONBytes(body));
    }
}
