package com.sipa.boot.java8.iot.core.message.device;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class DeviceLogMessage extends CommonDeviceMessage {
    private String log;

    @Override
    public EMessageType getMessageType() {
        return EMessageType.LOG;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
