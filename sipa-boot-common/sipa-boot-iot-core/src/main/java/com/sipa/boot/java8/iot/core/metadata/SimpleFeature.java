package com.sipa.boot.java8.iot.core.metadata;

import com.sipa.boot.java8.iot.core.metadata.base.IFeature;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class SimpleFeature implements IFeature {
    private String id;

    private String name;

    public SimpleFeature() {}

    public SimpleFeature(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
