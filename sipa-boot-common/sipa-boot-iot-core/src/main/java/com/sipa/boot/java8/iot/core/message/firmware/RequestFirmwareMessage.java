package com.sipa.boot.java8.iot.core.message.firmware;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IRepayableDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class RequestFirmwareMessage extends CommonDeviceMessage
    implements IRepayableDeviceMessage<RequestFirmwareMessageReply> {
    private String currentVersion;

    private String requestVersion;

    @Override
    public EMessageType getMessageType() {
        return EMessageType.REQUEST_FIRMWARE;
    }

    @Override
    public RequestFirmwareMessageReply newReply() {
        return new RequestFirmwareMessageReply().from(this);
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getRequestVersion() {
        return requestVersion;
    }

    public void setRequestVersion(String requestVersion) {
        this.requestVersion = requestVersion;
    }
}
