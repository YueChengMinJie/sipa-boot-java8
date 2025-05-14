package com.sipa.boot.java8.iot.core.message.device;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class DerivedMetadataMessage extends CommonDeviceMessage {
    private String metadata;

    private boolean all;

    @Override
    public EMessageType getMessageType() {
        return EMessageType.DERIVED_METADATA;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }
}
