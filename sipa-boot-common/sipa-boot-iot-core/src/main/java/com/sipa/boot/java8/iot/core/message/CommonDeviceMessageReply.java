package com.sipa.boot.java8.iot.core.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.bean.FastBeanCopier;
import com.sipa.boot.java8.iot.core.enumerate.EErrorCode;
import com.sipa.boot.java8.iot.core.exception.DeviceOperationException;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessageReply;
import com.sipa.boot.java8.iot.core.message.base.IHeaderKey;
import com.sipa.boot.java8.iot.core.message.base.IMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class CommonDeviceMessageReply<ME extends CommonDeviceMessageReply> implements IDeviceMessageReply {
    private static final long serialVersionUID = -6849794470754667710L;

    private boolean success = true;

    private String code;

    private String message;

    private String messageId;

    private String deviceId;

    private long timestamp = System.currentTimeMillis();

    private Map<String, Object> headers;

    public CommonDeviceMessageReply() {}

    public CommonDeviceMessageReply(boolean success, String code, String message, String messageId, String deviceId,
        long timestamp, Map<String, Object> headers) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.messageId = messageId;
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.headers = headers;
    }

    @Override
    public synchronized ME addHeaderIfAbsent(String header, Object value) {
        if (headers == null) {
            this.headers = new ConcurrentHashMap<>();
        }
        if (header != null && value != null) {
            this.headers.putIfAbsent(header, value);
        }
        return (ME)this;
    }

    @Override
    public synchronized ME addHeader(String header, Object value) {
        if (headers == null) {
            this.headers = new ConcurrentHashMap<>();
        }
        if (header != null && value != null) {
            this.headers.put(header, value);
        }
        return (ME)this;
    }

    @Override
    public ME removeHeader(String header) {
        if (headers != null) {
            this.headers.remove(header);
        }
        return (ME)this;
    }

    public ME code(String code) {
        this.code = code;

        return (ME)this;
    }

    public ME message(String message) {
        this.message = message;

        return (ME)this;
    }

    public ME deviceId(String deviceId) {
        this.deviceId = deviceId;

        return (ME)this;
    }

    @Override
    public ME success() {
        success = true;
        return (ME)this;
    }

    public ME error(Throwable e) {
        success = false;
        if (e instanceof DeviceOperationException) {
            error(((DeviceOperationException)e).getCode());
        } else {
            error(EErrorCode.SYSTEM_ERROR);
        }
        setMessage(e.getMessage());
        addHeader("errorType", e.getClass().getName());
        addHeader("errorMessage", e.getMessage());

        return ((ME)this);
    }

    @Override
    public ME error(EErrorCode errorCode) {
        success = false;
        code = errorCode.name();
        message = errorCode.getText();
        timestamp = System.currentTimeMillis();
        return (ME)this;
    }

    @Override
    public ME from(IMessage message) {
        this.messageId = message.getMessageId();
        if (message instanceof IDeviceMessage) {
            this.deviceId = ((IDeviceMessage)message).getDeviceId();
        }

        return (ME)this;
    }

    @Override
    public ME messageId(String messageId) {
        this.messageId = messageId;
        return (ME)this;
    }

    @Override
    public <T> ME addHeader(IHeaderKey<T> header, T value) {
        return (ME)IDeviceMessageReply.super.addHeader(header, value);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = FastBeanCopier.copy(this, JSONObject::new);
        json.put("messageType", getMessageType().name());
        return json;
    }

    @Override
    public void fromJson(JSONObject jsonObject) {
        IDeviceMessageReply.super.fromJson(jsonObject);
        success = jsonObject.getBooleanValue("success");

        timestamp = jsonObject.getLongValue("timestamp");
        if (timestamp == 0) {
            timestamp = System.currentTimeMillis();
        }
        messageId = jsonObject.getString("messageId");
        deviceId = jsonObject.getString("deviceId");
        code = jsonObject.getString("code");
        message = jsonObject.getString("message");
        headers = jsonObject.getJSONObject("headers");
    }

    @Override
    public String toString() {
        return toJson().toJSONString();
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public static final class CommonDeviceMessageReplyBuilder {
        private boolean success = true;

        private String code;

        private String message;

        private String messageId;

        private String deviceId;

        private long timestamp = System.currentTimeMillis();

        private Map<String, Object> headers;

        private CommonDeviceMessageReplyBuilder() {}

        public static CommonDeviceMessageReplyBuilder aCommonDeviceMessageReply() {
            return new CommonDeviceMessageReplyBuilder();
        }

        public CommonDeviceMessageReplyBuilder withSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public CommonDeviceMessageReplyBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public CommonDeviceMessageReplyBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public CommonDeviceMessageReplyBuilder withMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public CommonDeviceMessageReplyBuilder withDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public CommonDeviceMessageReplyBuilder withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public CommonDeviceMessageReplyBuilder withHeaders(Map<String, Object> headers) {
            this.headers = headers;
            return this;
        }

        public CommonDeviceMessageReply build() {
            CommonDeviceMessageReply commonDeviceMessageReply = new CommonDeviceMessageReply();
            commonDeviceMessageReply.setSuccess(success);
            commonDeviceMessageReply.setCode(code);
            commonDeviceMessageReply.setMessage(message);
            commonDeviceMessageReply.setMessageId(messageId);
            commonDeviceMessageReply.setDeviceId(deviceId);
            commonDeviceMessageReply.setTimestamp(timestamp);
            commonDeviceMessageReply.setHeaders(headers);
            return commonDeviceMessageReply;
        }
    }
}
