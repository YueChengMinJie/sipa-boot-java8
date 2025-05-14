package com.sipa.boot.java8.common.exceptions;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class LimitExceededException extends ForbiddenException {
    public LimitExceededException(String resource) {
        super(10013, "errors.com.sipa.boot.limit_exceeded", "Resource '{}' exceeded max limit.", resource);
    }
}
