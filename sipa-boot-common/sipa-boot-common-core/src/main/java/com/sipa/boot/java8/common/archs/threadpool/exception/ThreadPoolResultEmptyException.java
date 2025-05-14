package com.sipa.boot.java8.common.archs.threadpool.exception;

/**
 * @author sunyukun
 * @since 2019/8/6 15:02
 */
public class ThreadPoolResultEmptyException extends RuntimeException {
    public ThreadPoolResultEmptyException() {
        super("result is empty");
    }
}
