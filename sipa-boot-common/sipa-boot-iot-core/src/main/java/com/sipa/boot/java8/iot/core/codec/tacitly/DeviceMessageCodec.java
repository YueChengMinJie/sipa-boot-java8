package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.event.base.IPayload;
import com.sipa.boot.java8.iot.core.message.base.IMessage;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class DeviceMessageCodec implements ICodec<IMessage> {
    public static DeviceMessageCodec INSTANCE = new DeviceMessageCodec();

    private DeviceMessageCodec() {}

    @Override
    public Class<IMessage> forType() {
        return IMessage.class;
    }

    @Nullable
    @Override
    public IMessage decode(@Nonnull IPayload payload) {
        JSONObject json = JSON.parseObject(payload.bodyToString(false));
        return EMessageType.convertMessage(json)
            .orElseThrow(() -> new UnsupportedOperationException("unsupported message : " + json));
    }

    @Override
    public IPayload encode(IMessage body) {
        return IPayload.of(JSON.toJSONBytes(body.toJson()));
    }
}
