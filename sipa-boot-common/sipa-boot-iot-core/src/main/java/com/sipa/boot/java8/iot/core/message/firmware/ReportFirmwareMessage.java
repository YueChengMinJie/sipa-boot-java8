package com.sipa.boot.java8.iot.core.message.firmware;

import java.util.Map;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class ReportFirmwareMessage extends CommonDeviceMessage {
    private String version;

    private Map<String, Object> properties;

    @Override
    public EMessageType getMessageType() {
        return EMessageType.REPORT_FIRMWARE;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
