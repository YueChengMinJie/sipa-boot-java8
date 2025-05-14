package com.sipa.boot.java8.common.ws.property;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhouxiajie
 * @date 2019-01-22
 */
@ConfigurationProperties(prefix = "sipa.boot.ws")
@Component
public class WsProperties {
    private String url;

    private String appPrefix;

    private List<String> simpleBrokers;

    public String getAppPrefix() {
        return appPrefix;
    }

    public void setAppPrefix(String appPrefix) {
        this.appPrefix = appPrefix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getSimpleBrokers() {
        return simpleBrokers;
    }

    public void setSimpleBrokers(List<String> simpleBrokers) {
        this.simpleBrokers = simpleBrokers;
    }
}
