package com.sipa.boot.java8.iot.core.device;

import java.io.Serializable;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class DeviceStateInfo implements Serializable {
    private static final long serialVersionUID = -4723139810742934849L;

    private String deviceId;

    private byte state;

    public DeviceStateInfo(String deviceId, byte state) {
        this.deviceId = deviceId;
        this.state = state;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }
}
