package com.sipa.boot.java8.common.auth.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * @author zhouxiajie
 * @date 2018/2/5
 */
public class MethodNotSupportedException extends AuthenticationServiceException {
    private static final long serialVersionUID = 3705043083010304496L;

    public MethodNotSupportedException(String msg) {
        super(msg);
    }
}
