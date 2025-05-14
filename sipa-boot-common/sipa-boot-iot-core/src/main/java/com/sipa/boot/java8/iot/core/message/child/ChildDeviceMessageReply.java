package com.sipa.boot.java8.iot.core.message.child;

import java.util.function.BiConsumer;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.enumerate.EErrorCode;
import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessageReply;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessageReply;
import com.sipa.boot.java8.iot.core.message.base.IMessage;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class ChildDeviceMessageReply extends CommonDeviceMessageReply<ChildDeviceMessageReply> {
    private String childDeviceId;

    private IMessage childDeviceMessage;

    @Override
    public EMessageType getMessageType() {
        return EMessageType.CHILD_REPLY;
    }

    @Override
    public ChildDeviceMessageReply error(Throwable e) {
        doWithChildReply(e, IDeviceMessageReply::error);
        return super.error(e);
    }

    @Override
    public ChildDeviceMessageReply error(EErrorCode errorCode) {
        doWithChildReply(errorCode, IDeviceMessageReply::error);
        return super.error(errorCode);
    }

    @Override
    public ChildDeviceMessageReply message(String message) {
        doWithChildReply(message, IDeviceMessageReply::message);
        return super.message(message);
    }

    @Override
    public ChildDeviceMessageReply code(String code) {
        doWithChildReply(code, IDeviceMessageReply::code);
        return super.code(code);
    }

    public <T> void doWithChildReply(T arg, BiConsumer<IDeviceMessageReply, T> childReplyConsumer) {
        if (childDeviceMessage instanceof IDeviceMessageReply) {
            IDeviceMessageReply child = ((IDeviceMessageReply)childDeviceMessage);
            childReplyConsumer.accept(child, arg);
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        if (null != childDeviceMessage) {
            json.put("childDeviceMessage", childDeviceMessage.toJson());
        }
        return json;
    }

    public String getChildDeviceId() {
        return childDeviceId;
    }

    public void setChildDeviceId(String childDeviceId) {
        this.childDeviceId = childDeviceId;
    }

    public IMessage getChildDeviceMessage() {
        return childDeviceMessage;
    }

    public void setChildDeviceMessage(IMessage childDeviceMessage) {
        this.childDeviceMessage = childDeviceMessage;
    }
}
