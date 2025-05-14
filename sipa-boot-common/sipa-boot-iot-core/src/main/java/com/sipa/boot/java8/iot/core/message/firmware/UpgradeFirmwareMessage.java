package com.sipa.boot.java8.iot.core.message.firmware;

import java.util.Map;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IRepayableDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class UpgradeFirmwareMessage extends CommonDeviceMessage
    implements IRepayableDeviceMessage<UpgradeFirmwareMessageReply> {
    private String url;

    private String version;

    private Map<String, Object> parameters;

    private String sign;

    private String signMethod;

    private String firmwareId;

    private long size;

    @Override
    public UpgradeFirmwareMessageReply newReply() {
        return new UpgradeFirmwareMessageReply().from(this);
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.UPGRADE_FIRMWARE;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignMethod() {
        return signMethod;
    }

    public void setSignMethod(String signMethod) {
        this.signMethod = signMethod;
    }

    public String getFirmwareId() {
        return firmwareId;
    }

    public void setFirmwareId(String firmwareId) {
        this.firmwareId = firmwareId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
