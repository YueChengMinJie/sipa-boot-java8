package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class FastJsonCodec implements ICodec<JSONObject> {
    public static final FastJsonCodec INSTANCE = new FastJsonCodec();

    @Override
    public Class<JSONObject> forType() {
        return JSONObject.class;
    }

    @Override
    public JSONObject decode(@Nonnull IPayload payload) {
        return JSON.parseObject(payload.bodyToString(false));
    }

    @Override
    public IPayload encode(JSONObject body) {
        return IPayload.of(JSON.toJSONBytes(body));
    }
}
