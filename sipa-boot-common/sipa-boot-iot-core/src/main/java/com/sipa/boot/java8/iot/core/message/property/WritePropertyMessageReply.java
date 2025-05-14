package com.sipa.boot.java8.iot.core.message.property;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessageReply;
import com.sipa.boot.java8.iot.core.message.property.base.IPropertyMessage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class WritePropertyMessageReply extends CommonDeviceMessageReply<WritePropertyMessageReply>
    implements IPropertyMessage {
    private Map<String, Object> properties;

    private Map<String, Long> propertySourceTimes;

    private Map<String, String> propertyStates;

    public synchronized WritePropertyMessageReply addProperty(String key, Object value) {
        if (properties == null) {
            properties = new LinkedHashMap<>();
        }
        properties.put(key, value);
        return this;
    }

    public static WritePropertyMessageReply create() {
        WritePropertyMessageReply reply = new WritePropertyMessageReply();

        reply.setTimestamp(System.currentTimeMillis());

        return reply;
    }

    @Override
    public void fromJson(JSONObject jsonObject) {
        super.fromJson(jsonObject);
        this.properties = jsonObject.getJSONObject("properties");
        this.propertySourceTimes = (Map)jsonObject.getJSONObject("propertySourceTimes");
        this.propertyStates = (Map)jsonObject.getJSONObject("propertyStates");
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.WRITE_PROPERTY_REPLY;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, Long> getPropertySourceTimes() {
        return propertySourceTimes;
    }

    public void setPropertySourceTimes(Map<String, Long> propertySourceTimes) {
        this.propertySourceTimes = propertySourceTimes;
    }

    @Override
    public Map<String, String> getPropertyStates() {
        return propertyStates;
    }

    public void setPropertyStates(Map<String, String> propertyStates) {
        this.propertyStates = propertyStates;
    }
}
