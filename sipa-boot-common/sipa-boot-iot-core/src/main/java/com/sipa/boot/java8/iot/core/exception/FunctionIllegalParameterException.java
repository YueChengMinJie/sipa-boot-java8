package com.sipa.boot.java8.iot.core.exception;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class FunctionIllegalParameterException extends IllegalParameterException {
    public FunctionIllegalParameterException(String parameter, String message) {
        super(parameter, message);
    }
}
