package com.sipa.boot.java8.common.oauth2.enumerate;

/**
 * @author feizhihao
 * @date 2019-05-10
 */
public enum ELoginStrategy {
    /**
     * 有状态登陆
     */
    STATEFUL("stateful"),

    /**
     * 无状态登陆
     */
    STATELESS("stateless");

    private final String code;

    ELoginStrategy(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
