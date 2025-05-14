package com.sipa.boot.java8.iot.core.exception;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class IllegalParameterException extends IllegalArgumentException {
    private String parameter;

    public IllegalParameterException(String parameter, String message) {
        super(message);
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
