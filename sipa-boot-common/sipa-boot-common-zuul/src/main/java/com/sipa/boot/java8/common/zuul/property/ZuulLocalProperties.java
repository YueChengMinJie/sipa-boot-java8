package com.sipa.boot.java8.common.zuul.property;

/**
 * @author zhouxiajie
 * @date 2019/10/4
 */
public class ZuulLocalProperties {
    private boolean enable;

    private String type;

    private String keyPath;

    private String checkUrl;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    public String getCheckUrl() {
        return checkUrl;
    }

    public void setCheckUrl(String checkUrl) {
        this.checkUrl = checkUrl;
    }
}
