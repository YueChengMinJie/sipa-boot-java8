package com.sipa.boot.java8.common.exceptions;

import java.util.Optional;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * An exception thrown to indicate that an attempt has been made to access a to which the user has no access.
 * <p>
 */
public class ForbiddenException extends ApplicationException {
    public static final int DEFAULT_FORBIDDEN_EXCEPTION_STATUS_CODE = SipaBootCommonConstants.Http.FORBIDDEN;

    public static final int DEFAULT_FORBIDDEN_EXCEPTION_NUMERIC_ERROR_CODE = 10004;

    public static final String DEFAULT_FORBIDDEN_EXCEPTION_ERROR_CODE = "errors.com.sipa.boot.forbidden";

    public static final String DEFAULT_FORBIDDEN_EXCEPTION_ERROR_MESSAGE = "Access forbidden.";

    public ForbiddenException() {
        this(DEFAULT_FORBIDDEN_EXCEPTION_ERROR_MESSAGE);
    }

    public ForbiddenException(String pattern, Object... args) {
        this(DEFAULT_FORBIDDEN_EXCEPTION_NUMERIC_ERROR_CODE, DEFAULT_FORBIDDEN_EXCEPTION_ERROR_CODE, pattern, args);
    }

    public ForbiddenException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(DEFAULT_FORBIDDEN_EXCEPTION_STATUS_CODE, numericErrCode,
            Optional.ofNullable(errCode).orElse(DEFAULT_FORBIDDEN_EXCEPTION_ERROR_CODE), pattern, args);
    }
}
