package com.sipa.boot.java8.iot.core.message.child;

import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.enumerate.EErrorCode;
import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.exception.DeviceOperationException;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IMessage;
import com.sipa.boot.java8.iot.core.message.base.IRepayableDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class ChildDeviceMessage extends CommonDeviceMessage
    implements IRepayableDeviceMessage<ChildDeviceMessageReply> {
    private String childDeviceId;

    private IMessage childDeviceMessage;

    public static ChildDeviceMessage create(String deviceId, IDeviceMessage message) {
        ChildDeviceMessage msg = new ChildDeviceMessage();
        msg.setDeviceId(deviceId);
        msg.setMessageId(message.getMessageId());
        msg.setChildDeviceId(message.getDeviceId());
        msg.setChildDeviceMessage(message);
        return msg;
    }

    @Override
    public ChildDeviceMessageReply newReply() {
        ChildDeviceMessageReply reply = new ChildDeviceMessageReply();
        if (childDeviceMessage instanceof IRepayableDeviceMessage) {
            reply.setChildDeviceMessage(((IRepayableDeviceMessage<?>)childDeviceMessage).newReply());
        }
        reply.messageId(getMessageId());
        reply.deviceId(getDeviceId());
        reply.setChildDeviceId(getChildDeviceId());
        return reply;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        if (null != childDeviceMessage) {
            json.put("childDeviceMessage", childDeviceMessage.toJson());
        }
        return json;
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.CHILD;
    }

    @Override
    public void validate() {
        if (childDeviceMessage instanceof ChildDeviceMessage) {
            Set<String> deviceId = new HashSet<>();
            IMessage msg = childDeviceMessage;
            do {
                String childId = ((ChildDeviceMessage)msg).getChildDeviceId();
                msg = ((ChildDeviceMessage)msg).getChildDeviceMessage();
                if (deviceId.contains(childId)) {
                    throw new DeviceOperationException(EErrorCode.CYCLIC_DEPENDENCE);
                }
                deviceId.add(childId);
            } while (msg instanceof ChildDeviceMessage);
        }
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
