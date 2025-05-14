package com.sipa.boot.java8.iot.core.metadata.type.base;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.sipa.boot.java8.iot.core.config.base.IConfigKey;
import com.sipa.boot.java8.iot.core.config.base.IConfigKeyValue;
import com.sipa.boot.java8.iot.core.metadata.base.IDataType;

public abstract class AbstractType<R> implements IDataType {
    private Map<String, Object> expands;

    private String description;

    public R expands(Map<String, Object> expands) {
        if (CollectionUtils.isEmpty(expands)) {
            return (R)this;
        }
        if (this.expands == null) {
            this.expands = new HashMap<>();
        }
        this.expands.putAll(expands);
        return (R)this;
    }

    public R expand(IConfigKeyValue<?>... kvs) {
        for (IConfigKeyValue<?> kv : kvs) {
            expand(kv.getKey(), kv.getValue());
        }
        return (R)this;
    }

    public <V> R expand(IConfigKey<V> configKey, V value) {
        return expand(configKey.getKey(), value);
    }

    public R expand(String configKey, Object value) {
        if (value == null) {
            return (R)this;
        }
        if (expands == null) {
            expands = new HashMap<>();
        }
        expands.put(configKey, value);
        return (R)this;
    }

    public R description(String description) {
        this.description = description;
        return (R)this;
    }

    @Override
    public Map<String, Object> getExpands() {
        return expands;
    }

    @Override
    public void setExpands(Map<String, Object> expands) {
        this.expands = expands;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
