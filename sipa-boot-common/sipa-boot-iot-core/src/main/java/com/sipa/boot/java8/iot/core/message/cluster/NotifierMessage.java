package com.sipa.boot.java8.iot.core.message.cluster;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class NotifierMessage implements Serializable {
    private String messageId;

    private String fromServer;

    private String address;

    private Object payload;

    public NotifierMessage() {}

    public NotifierMessage(String messageId, String fromServer, String address, Object payload) {
        this.messageId = messageId;
        this.fromServer = fromServer;
        this.address = address;
        this.payload = payload;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFromServer() {
        return fromServer;
    }

    public void setFromServer(String fromServer) {
        this.fromServer = fromServer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("messageId", messageId)
            .append("fromServer", fromServer)
            .append("address", address)
            .append("payload", payload)
            .toString();
    }
}
