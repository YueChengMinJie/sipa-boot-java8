package com.sipa.boot.java8.iot.core.exception;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class FunctionUndefinedException extends IllegalArgumentException {
    private String function;

    public FunctionUndefinedException(String function, String message) {
        super(message);
        this.function = function;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
