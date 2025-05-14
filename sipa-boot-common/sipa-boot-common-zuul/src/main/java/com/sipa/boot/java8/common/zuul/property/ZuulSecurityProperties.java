package com.sipa.boot.java8.common.zuul.property;

import com.sipa.boot.java8.common.oauth2.enumerate.ELoginStrategy;

/**
 * @author zhouxiajie
 * @date 2019/10/4
 */
public class ZuulSecurityProperties {
    private ELoginStrategy loginStrategy;

    private String ignoring;

    private String intercept;

    public ELoginStrategy getLoginStrategy() {
        return loginStrategy;
    }

    public void setLoginStrategy(ELoginStrategy loginStrategy) {
        this.loginStrategy = loginStrategy;
    }

    public String getIgnoring() {
        return ignoring;
    }

    public void setIgnoring(String ignoring) {
        this.ignoring = ignoring;
    }

    public String getIntercept() {
        return intercept;
    }

    public void setIntercept(String intercept) {
        this.intercept = intercept;
    }
}
