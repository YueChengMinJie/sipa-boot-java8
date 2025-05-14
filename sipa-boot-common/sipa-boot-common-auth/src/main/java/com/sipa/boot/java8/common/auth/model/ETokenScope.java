package com.sipa.boot.java8.common.auth.model;

/**
 * @author zhouxiajie
 * @date 2018/2/10
 */
public enum ETokenScope {
    // token 类型
    ACCESS_TOKEN, REFRESH_TOKEN;

    public String scope() {
        return "SCOPE_" + this.name();
    }
}
