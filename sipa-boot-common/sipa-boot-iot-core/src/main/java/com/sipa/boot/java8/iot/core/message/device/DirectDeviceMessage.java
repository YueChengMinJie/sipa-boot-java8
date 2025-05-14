package com.sipa.boot.java8.iot.core.message.device;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class DirectDeviceMessage extends CommonDeviceMessage {
    private byte[] payload;

    @Override
    public EMessageType getMessageType() {
        return EMessageType.DIRECT;
    }

    @Override
    public void fromJson(JSONObject jsonObject) {
        setPayload(jsonObject.getBytes("payload"));
        setDeviceId(jsonObject.getString("deviceId"));
        setMessageId(jsonObject.getString("messageId"));
        Long ts = jsonObject.getLong("timestamp");
        if (null != ts) {
            setTimestamp(ts);
        }
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
