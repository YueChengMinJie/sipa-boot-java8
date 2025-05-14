package com.sipa.boot.java8.common.zuul.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author zhouxiajie
 * @date 2019/10/4
 */
@ConfigurationProperties(prefix = "sipa.boot.zuul")
public class ZuulProperties {
    private boolean enableCanary;

    private boolean enableIdempotent;

    private boolean enableRbac;

    @NestedConfigurationProperty
    private ZuulSecurityProperties security = new ZuulSecurityProperties();

    @NestedConfigurationProperty
    private ZuulLocalProperties local = new ZuulLocalProperties();

    public boolean isEnableCanary() {
        return enableCanary;
    }

    public void setEnableCanary(boolean enableCanary) {
        this.enableCanary = enableCanary;
    }

    public boolean isEnableIdempotent() {
        return enableIdempotent;
    }

    public void setEnableIdempotent(boolean enableIdempotent) {
        this.enableIdempotent = enableIdempotent;
    }

    public ZuulSecurityProperties getSecurity() {
        return security;
    }

    public void setSecurity(ZuulSecurityProperties security) {
        this.security = security;
    }

    public boolean isEnableRbac() {
        return enableRbac;
    }

    public void setEnableRbac(boolean enableRbac) {
        this.enableRbac = enableRbac;
    }

    public ZuulLocalProperties getLocal() {
        return local;
    }

    public void setLocal(ZuulLocalProperties local) {
        this.local = local;
    }
}
