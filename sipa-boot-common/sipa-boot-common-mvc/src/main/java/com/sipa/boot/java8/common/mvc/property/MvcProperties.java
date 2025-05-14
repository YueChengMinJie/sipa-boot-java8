package com.sipa.boot.java8.common.mvc.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.mvc.constant.SipaBootMvcConstants;

/**
 * @author zhouxiajie
 * @date 2019/10/4
 */
@Component
@ConfigurationProperties(prefix = SipaBootMvcConstants.MVC_PREFIX)
public class MvcProperties {
    @NestedConfigurationProperty
    private MvcRequestProperties request = new MvcRequestProperties();

    public MvcRequestProperties getSipaBootMvcRequestProperties() {
        return request;
    }

    public void setSipaBootMvcRequestProperties(MvcRequestProperties mvcRequestProperties) {
        this.request = mvcRequestProperties;
    }
}
