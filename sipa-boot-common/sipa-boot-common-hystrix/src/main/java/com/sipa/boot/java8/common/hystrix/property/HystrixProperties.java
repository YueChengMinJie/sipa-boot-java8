package com.sipa.boot.java8.common.hystrix.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhouxiajie
 * @date 2019/10/4
 */
@Component
@ConfigurationProperties(prefix = "sipa.boot.hystrix")
public class HystrixProperties {
    private String urlMappings = "/hystrix.stream";

    public String getUrlMappings() {
        return urlMappings;
    }

    public void setUrlMappings(String urlMappings) {
        this.urlMappings = urlMappings;
    }
}
