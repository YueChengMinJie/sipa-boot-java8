package com.sipa.boot.java8.iot.core.message.device;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IRepayableDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class DisconnectDeviceMessage extends CommonDeviceMessage
    implements IRepayableDeviceMessage<DisconnectDeviceMessageReply> {
    @Override
    public DisconnectDeviceMessageReply newReply() {
        return new DisconnectDeviceMessageReply().from(this);
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.DISCONNECT;
    }
}
