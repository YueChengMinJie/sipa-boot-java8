package com.sipa.boot.java8.iot.core.message.property;

import com.sipa.boot.java8.iot.core.message.property.base.IProperty;

/**
 * @author caszhou
 * @date 2021/9/23
 */
public class SimpleProperty implements IProperty {
    private String id;

    private Object value;

    private long timestamp;

    private String state;

    public SimpleProperty() {}

    public SimpleProperty(String id, Object value, long timestamp, String state) {
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
        this.state = state;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
