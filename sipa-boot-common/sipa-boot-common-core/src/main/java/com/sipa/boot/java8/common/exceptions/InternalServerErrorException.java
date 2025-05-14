package com.sipa.boot.java8.common.exceptions;

import java.util.Optional;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class InternalServerErrorException extends ApplicationException {
    public static final int DEFAULT_INTERNAL_SERVER_ERROR_EXCEPTION_STATUS_CODE =
        SipaBootCommonConstants.Http.INTERNAL_SERVER_ERROR;

    public static final int DEFAULT_INTERNAL_SERVER_ERROR_EXCEPTION_NUMERIC_ERROR_CODE = 10010;

    public static final String DEFAULT_INTERNAL_SERVER_ERROR_EXCEPTION_ERROR_CODE =
        "errors.com.sipa.boot.internal_server_error";

    public static final String DEFAULT_INTERNAL_SERVER_ERROR_EXCEPTION_ERROR_MESSAGE = "Internal server error.";

    public InternalServerErrorException() {
        this(DEFAULT_INTERNAL_SERVER_ERROR_EXCEPTION_ERROR_MESSAGE);
    }

    public InternalServerErrorException(String pattern, Object... args) {
        this(DEFAULT_INTERNAL_SERVER_ERROR_EXCEPTION_NUMERIC_ERROR_CODE,
            DEFAULT_INTERNAL_SERVER_ERROR_EXCEPTION_ERROR_CODE, pattern, args);
    }

    public InternalServerErrorException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(DEFAULT_INTERNAL_SERVER_ERROR_EXCEPTION_STATUS_CODE, numericErrCode,
            Optional.ofNullable(errCode).orElse(DEFAULT_INTERNAL_SERVER_ERROR_EXCEPTION_ERROR_CODE), pattern, args);
    }
}
