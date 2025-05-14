package com.sipa.boot.java8.iot.core.message.status;

import com.sipa.boot.java8.iot.core.constant.IDeviceState;
import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessageReply;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class DeviceStateCheckMessageReply extends CommonDeviceMessageReply<DeviceStateCheckMessageReply> {
    private byte state;

    public DeviceStateCheckMessageReply success(byte state) {
        this.state = state;
        return this;
    }

    public DeviceStateCheckMessageReply setOnline() {
        this.state = IDeviceState.online;
        return this;
    }

    public DeviceStateCheckMessageReply setOffline() {
        this.state = IDeviceState.offline;
        return this;
    }

    public DeviceStateCheckMessageReply setNoActive() {
        this.state = IDeviceState.noActive;
        return this;
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.STATE_CHECK_REPLY;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }
}
