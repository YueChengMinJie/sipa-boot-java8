package com.sipa.boot.java8.common.swagger.plugin.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhouxiajie
 * @date 2019-01-22
 */
@ConfigurationProperties(prefix = "sipa.boot.swagger")
@Component
public class SwaggerProperties {
    private String basePackage = "com.sipa.boot";

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
