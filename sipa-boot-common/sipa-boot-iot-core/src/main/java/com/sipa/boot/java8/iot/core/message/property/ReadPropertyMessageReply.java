package com.sipa.boot.java8.iot.core.message.property;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessageReply;
import com.sipa.boot.java8.iot.core.message.property.base.IPropertyMessage;

import java.util.Map;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class ReadPropertyMessageReply extends CommonDeviceMessageReply<ReadPropertyMessageReply>
    implements IPropertyMessage {
    private Map<String, Object> properties;

    private Map<String, Long> propertySourceTimes;

    private Map<String, String> propertyStates;

    public static ReadPropertyMessageReply create() {
        ReadPropertyMessageReply reply = new ReadPropertyMessageReply();

        reply.setTimestamp(System.currentTimeMillis());

        return reply;
    }

    public ReadPropertyMessageReply success(Map<String, Object> properties) {
        this.properties = properties;
        super.setSuccess(true);
        return this;
    }

    @Override
    public void fromJson(JSONObject jsonObject) {
        super.fromJson(jsonObject);
        this.properties = jsonObject.getJSONObject("properties");
        this.propertySourceTimes = (Map)jsonObject.getJSONObject("propertySourceTimes");
        this.propertyStates = (Map)jsonObject.getJSONObject("propertyStates");
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
