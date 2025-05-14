package com.sipa.boot.java8.iot.core.message.device;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessageReply;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class DisconnectDeviceMessageReply extends CommonDeviceMessageReply<DisconnectDeviceMessageReply> {
    @Override
    public EMessageType getMessageType() {
        return EMessageType.DISCONNECT_REPLY;
    }
}
