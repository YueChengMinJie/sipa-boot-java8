package com.sipa.boot.java8.common.exceptions;

/**
 * Exception thrown when a count parameter that is too big or small is used.
 * <p>
 */
public class InvalidCountException extends BadRequestException {
    public static final int DEFAULT_INVALID_COUNT_EXCEPTION_NUMERIC_ERROR_CODE = 10008;

    public InvalidCountException(int count) {
        super(DEFAULT_INVALID_COUNT_EXCEPTION_NUMERIC_ERROR_CODE, "errors.com.sipa.boot.invalid_count",
            "The count '{}' should be greater than 0 and not greater than 1000.", count);
    }
}
