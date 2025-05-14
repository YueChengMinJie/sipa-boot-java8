package com.sipa.boot.java8.common.exceptions;

import java.util.Optional;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * An exception thrown to indicate that a bad request was made to the application.
 * <p>
 */
public class BadRequestException extends ApplicationException {
    public static final int DEFAULT_BAD_REQUEST_EXCEPTION_STATUS_CODE = SipaBootCommonConstants.Http.BAD_REQUEST;

    public static final int DEFAULT_BAD_REQUEST_EXCEPTION_NUMERIC_ERROR_CODE = 10001;

    public static final String DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_CODE = "errors.com.sipa.boot.bad_request";

    public static final String DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_MESSAGE = "Bad request.";

    public BadRequestException() {
        this(DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_MESSAGE);
    }

    public BadRequestException(String pattern, Object... args) {
        this(DEFAULT_BAD_REQUEST_EXCEPTION_NUMERIC_ERROR_CODE, DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_CODE, pattern, args);
    }

    public BadRequestException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(DEFAULT_BAD_REQUEST_EXCEPTION_STATUS_CODE, numericErrCode,
            Optional.ofNullable(errCode).orElse(DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_CODE), pattern, args);
    }
}
