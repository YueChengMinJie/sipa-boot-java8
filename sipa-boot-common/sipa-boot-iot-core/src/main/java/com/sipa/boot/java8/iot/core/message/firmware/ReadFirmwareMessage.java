package com.sipa.boot.java8.iot.core.message.firmware;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IRepayableDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class ReadFirmwareMessage extends CommonDeviceMessage
    implements IRepayableDeviceMessage<ReadFirmwareMessageReply> {
    @Override
    public ReadFirmwareMessageReply newReply() {
        return new ReadFirmwareMessageReply().from(this);
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.READ_FIRMWARE;
    }
}
