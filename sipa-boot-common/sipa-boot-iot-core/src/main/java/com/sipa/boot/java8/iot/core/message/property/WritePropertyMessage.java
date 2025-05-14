package com.sipa.boot.java8.iot.core.message.property;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IRepayableDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class WritePropertyMessage extends CommonDeviceMessage
    implements IRepayableDeviceMessage<WritePropertyMessageReply> {
    private Map<String, Object> properties = new LinkedHashMap<>();

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    @Override
    public WritePropertyMessageReply newReply() {
        return new WritePropertyMessageReply().from(this);
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.WRITE_PROPERTY;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
