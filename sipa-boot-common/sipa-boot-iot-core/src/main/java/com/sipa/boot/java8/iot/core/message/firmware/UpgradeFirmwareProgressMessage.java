package com.sipa.boot.java8.iot.core.message.firmware;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class UpgradeFirmwareProgressMessage extends CommonDeviceMessage {
    private int progress;

    private boolean complete;

    private String version;

    private boolean success;

    private String errorReason;

    private String firmwareId;

    @Override
    public EMessageType getMessageType() {
        return EMessageType.UPGRADE_FIRMWARE_PROGRESS;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getFirmwareId() {
        return firmwareId;
    }

    public void setFirmwareId(String firmwareId) {
        this.firmwareId = firmwareId;
    }
}
