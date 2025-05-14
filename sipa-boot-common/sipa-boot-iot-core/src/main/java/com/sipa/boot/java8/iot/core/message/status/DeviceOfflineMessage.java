package com.sipa.boot.java8.iot.core.message.status;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class DeviceOfflineMessage extends CommonDeviceMessage {
    @Override
    public EMessageType getMessageType() {
        return EMessageType.OFFLINE;
    }
}
