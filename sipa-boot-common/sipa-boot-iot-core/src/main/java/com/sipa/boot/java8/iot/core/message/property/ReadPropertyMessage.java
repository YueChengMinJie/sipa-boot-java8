package com.sipa.boot.java8.iot.core.message.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IRepayableDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class ReadPropertyMessage extends CommonDeviceMessage
    implements IRepayableDeviceMessage<ReadPropertyMessageReply> {
    private List<String> properties = new ArrayList<>();

    public ReadPropertyMessage addProperties(List<String> properties) {
        this.properties.addAll(properties);
        return this;
    }

    public ReadPropertyMessage addProperties(String... properties) {
        return addProperties(Arrays.asList(properties));
    }

    @Override
    public ReadPropertyMessageReply newReply() {
        return new ReadPropertyMessageReply().from(this);
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.READ_PROPERTY;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
}
