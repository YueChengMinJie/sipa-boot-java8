package com.sipa.boot.java8.common.auth.exception;

import org.springframework.security.core.AuthenticationException;

import com.sipa.boot.java8.common.auth.model.token.JwtToken;

/**
 * @author zhouxiajie
 * @date 2018/2/5
 */
public class SipaBootExpiredJwtException extends AuthenticationException {
    private static final long serialVersionUID = -5959543783324224864L;

    private JwtToken token;

    public SipaBootExpiredJwtException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token.getToken();
    }
}
