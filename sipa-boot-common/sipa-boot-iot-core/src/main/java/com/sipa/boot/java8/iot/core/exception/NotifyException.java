package com.sipa.boot.java8.iot.core.exception;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class NotifyException extends RuntimeException {
    private String address;

    public NotifyException(String address, String message) {
        super(message);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
