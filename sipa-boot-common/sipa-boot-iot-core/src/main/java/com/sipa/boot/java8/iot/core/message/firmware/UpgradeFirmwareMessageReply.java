package com.sipa.boot.java8.iot.core.message.firmware;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessageReply;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class UpgradeFirmwareMessageReply extends CommonDeviceMessageReply<UpgradeFirmwareMessageReply> {
    @Override
    public EMessageType getMessageType() {
        return EMessageType.UPGRADE_FIRMWARE_REPLY;
    }
}
