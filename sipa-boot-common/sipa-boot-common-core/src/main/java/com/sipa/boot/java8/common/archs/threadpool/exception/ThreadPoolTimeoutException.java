package com.sipa.boot.java8.common.archs.threadpool.exception;

/**
 * @author sunyukun
 * @since 2019/8/6 15:02
 */
public class ThreadPoolTimeoutException extends RuntimeException {
    public ThreadPoolTimeoutException(String message) {
        super(message);
    }
}
