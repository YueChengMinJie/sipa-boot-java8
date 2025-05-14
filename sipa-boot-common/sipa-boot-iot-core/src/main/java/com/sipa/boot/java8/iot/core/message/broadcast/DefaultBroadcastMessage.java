package com.sipa.boot.java8.iot.core.message.broadcast;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.sipa.boot.java8.iot.core.message.base.IMessage;
import com.sipa.boot.java8.iot.core.message.broadcast.base.IBroadcastMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class DefaultBroadcastMessage implements IBroadcastMessage {
    private static final long serialVersionUID = -6849794470754667710L;

    private String messageId;

    private long timestamp = System.currentTimeMillis();

    private String address;

    private IMessage message;

    private Map<String, Object> headers;

    @Override
    public Map<String, Object> getHeaders() {
        return headers;
    }

    private Map<String, Object> safeGetHeader() {
        return headers == null ? headers = new HashMap<>() : headers;
    }

    @Override
    public synchronized IBroadcastMessage addHeader(String header, Object value) {
        if (StringUtils.isEmpty(header) || StringUtils.isEmpty(value)) {
            return this;
        }
        safeGetHeader().put(header, value);
        return this;
    }

    @Override
    public synchronized IBroadcastMessage addHeaderIfAbsent(String header, Object value) {
        if (StringUtils.isEmpty(header) || StringUtils.isEmpty(value)) {
            return this;
        }
        safeGetHeader().putIfAbsent(header, value);
        return this;
    }

    @Override
    public synchronized IBroadcastMessage removeHeader(String header) {
        if (StringUtils.isEmpty(header)) {
            return this;
        }
        safeGetHeader().remove(header);
        return this;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public IMessage getMessage() {
        return message;
    }

    public void setMessage(IMessage message) {
        this.message = message;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }
}
