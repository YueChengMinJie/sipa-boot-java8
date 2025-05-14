package com.sipa.boot.java8.iot.core.config;

import com.sipa.boot.java8.iot.core.config.base.IConfigKey;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class SimpleConfigKey<V> implements IConfigKey<V> {
    private String key;

    private String name;

    private Class<V> type;

    public SimpleConfigKey() {}

    public SimpleConfigKey(String key, String name, Class<V> type) {
        this.key = key;
        this.name = name;
        this.type = type;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Class<V> getType() {
        return type;
    }

    public void setType(Class<V> type) {
        this.type = type;
    }
}
