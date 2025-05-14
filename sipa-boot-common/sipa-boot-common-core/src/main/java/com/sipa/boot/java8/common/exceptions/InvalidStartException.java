package com.sipa.boot.java8.common.exceptions;

/**
 * Exception thrown when a negative start value is used.
 * <p>
 */
public class InvalidStartException extends BadRequestException {
    public static final int DEFAULT_INVALID_START_EXCEPTION_NUMERIC_ERROR_CODE = 10007;

    public InvalidStartException(int start) {
        super(DEFAULT_INVALID_START_EXCEPTION_NUMERIC_ERROR_CODE, "errors.com.sipa.boot.invalid_start",
            "The start '{}' should not be negative.", start);
    }
}
