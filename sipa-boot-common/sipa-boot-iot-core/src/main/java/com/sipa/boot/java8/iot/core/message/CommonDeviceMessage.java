package com.sipa.boot.java8.iot.core.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.iot.core.bean.FastBeanCopier;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessage;

/**
 * todo rd-2@10-9
 *
 * @author caszhou
 * @date 2021/9/24
 */
public class CommonDeviceMessage implements IDeviceMessage {
    private static final long serialVersionUID = -6849794470754667710L;

    private String code;

    private String messageId;

    private String deviceId;

    private Map<String, Object> headers;

    private long timestamp = System.currentTimeMillis();

    @Override
    public synchronized IDeviceMessage addHeader(String header, Object value) {
        if (headers == null) {
            this.headers = new ConcurrentHashMap<>(SipaBootCommonConstants.Number.INT_16);
        }
        if (header != null && value != null) {
            this.headers.put(header, value);
        }
        return this;
    }

    @Override
    public synchronized IDeviceMessage addHeaderIfAbsent(String header, Object value) {
        if (headers == null) {
            this.headers = new ConcurrentHashMap<>(SipaBootCommonConstants.Number.INT_16);
        }
        if (header != null && value != null) {
            this.headers.putIfAbsent(header, value);
        }
        return this;
    }

    @Override
    public IDeviceMessage removeHeader(String header) {
        if (this.headers != null) {
            this.headers.remove(header);
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = FastBeanCopier.copy(this, new JSONObject());
        json.put("messageType", getMessageType().name());
        return json;
    }

    @Override
    public void fromJson(JSONObject jsonObject) {
        IDeviceMessage.super.fromJson(jsonObject);
    }

    @Override
    public String toString() {
        return toJson().toJSONString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
