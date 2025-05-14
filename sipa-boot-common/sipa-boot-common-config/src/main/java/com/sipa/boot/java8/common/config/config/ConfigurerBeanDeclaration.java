package com.sipa.boot.java8.common.config.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.lang.StringUtils;

/**
 * @author caszhou
 * @date 2021/9/28
 */
class ConfigurerBeanDeclaration implements BeanDeclaration {
    private final Map<String, Object> properties = new HashMap<>();

    private String clsName;

    private String factoryName;

    @Override
    public String getBeanFactoryName() {
        return factoryName;
    }

    public void setBeanFactoryName(String factoryName) {
        if (StringUtils.isBlank(factoryName)) {
            throw new IllegalArgumentException("factoryName is blank or null");
        }
        this.factoryName = factoryName;
    }

    @Override
    public Object getBeanFactoryParameter() {
        return null;
    }

    @Override
    public String getBeanClassName() {
        return clsName;
    }

    public void setBeanClassName(String clsName) {
        if (StringUtils.isBlank(clsName)) {
            throw new IllegalArgumentException("clsName is blank or null");
        }
        this.clsName = clsName;
    }

    @Override
    public Map<String, Object> getBeanProperties() {
        return properties;
    }

    public void addBeanProperty(String key, Object value) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("key is blank or null");
        }
        properties.put(key, value);
    }

    @Override
    public Map<String, Object> getNestedBeanDeclarations() {
        return null;
    }
}
