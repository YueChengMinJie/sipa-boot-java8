package com.sipa.boot.java8.common.auth.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * @author zhouxiajie
 * @date 2021/6/13
 */
public class CustomUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private Integer type;

    private String remoteAddr;

    public CustomUsernamePasswordAuthenticationToken(Integer type, Object principal, Object credentials,
        String remoteAddr) {
        super(principal, credentials);
        this.type = type;
        this.remoteAddr = remoteAddr;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }
}
