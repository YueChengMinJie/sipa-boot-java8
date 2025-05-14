package com.sipa.boot.java8.common.auth.exception;

import org.springframework.security.authentication.BadCredentialsException;

/**
 * @author zhouxiajie
 * @date 2021/6/14
 */
public class SipaBootBadCredentialsException extends BadCredentialsException {
    public SipaBootBadCredentialsException(String msg) {
        super(msg);
    }

    public SipaBootBadCredentialsException(String msg, Throwable t) {
        super(msg, t);
    }
}
