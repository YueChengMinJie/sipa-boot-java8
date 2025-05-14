package com.sipa.boot.java8.iot.core.message.property;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;
import com.sipa.boot.java8.iot.core.message.property.base.IPropertyMessage;

/**
 * @author caszhou
 * @date 2021/9/23
 */
public class ReportPropertyMessage extends CommonDeviceMessage implements IPropertyMessage {
    private Map<String, Object> properties;

    private Map<String, Long> propertySourceTimes;

    private Map<String, String> propertyStates;

    public static ReportPropertyMessage create() {
        return new ReportPropertyMessage();
    }

    public ReportPropertyMessage success(Map<String, Object> properties) {
        this.properties = properties;
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
    public EMessageType getMessageType() {
        return EMessageType.REPORT_PROPERTY;
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
