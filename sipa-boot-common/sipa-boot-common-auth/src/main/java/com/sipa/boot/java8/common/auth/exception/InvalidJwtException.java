package com.sipa.boot.java8.common.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author zhouxiajie
 * @date 2018/2/5
 */
public class InvalidJwtException extends AuthenticationException {
    private static final long serialVersionUID = -294671188037098603L;

    public InvalidJwtException() {
        super("Invalid JWT Exception");
    }

    public InvalidJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
