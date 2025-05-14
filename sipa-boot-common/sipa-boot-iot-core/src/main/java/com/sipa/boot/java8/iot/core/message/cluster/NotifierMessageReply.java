package com.sipa.boot.java8.iot.core.message.cluster;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class NotifierMessageReply implements Serializable {
    private String address;

    private String messageId;

    private Object payload;

    private boolean success;

    private String errorMessage;

    private boolean complete;

    public NotifierMessageReply() {
    }

    public NotifierMessageReply(String address, String messageId, Object payload, boolean success, String errorMessage,
        boolean complete) {
        this.address = address;
        this.messageId = messageId;
        this.payload = payload;
        this.success = success;
        this.errorMessage = errorMessage;
        this.complete = complete;
    }

    public static NotifierMessageReply complete(String address, String messageId) {
        return new NotifierMessageReply(address, messageId, null, true, null, true);
    }

    public static NotifierMessageReply success(String address, String messageId, Object payload) {
        return new NotifierMessageReply(address, messageId, payload, true, null, false);
    }

    public static NotifierMessageReply fail(String address, String messageId, Throwable e) {
        return new NotifierMessageReply(address, messageId, null, false,
            e.getClass().getName().concat(":").concat(String.valueOf(e.getMessage())), false);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("address", address)
            .append("messageId", messageId)
            .append("payload", payload)
            .append("success", success)
            .append("errorMessage", errorMessage)
            .append("complete", complete)
            .toString();
    }
}
