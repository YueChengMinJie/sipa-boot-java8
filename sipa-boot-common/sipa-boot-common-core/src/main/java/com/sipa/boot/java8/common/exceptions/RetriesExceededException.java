package com.sipa.boot.java8.common.exceptions;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class RetriesExceededException extends BadRequestException {
    public RetriesExceededException(int retries) {
        super(10012, "errors.com.sipa.boot.retries_exceeded", "Retries '{}' was exceeded, please try later.", retries);
    }
}
