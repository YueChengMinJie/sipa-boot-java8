package com.sipa.boot.java8.common.exceptions;

import java.util.Optional;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * An exception thrown when an attempt is made to access an API with no authorization.
 * <p>
 */
public class UnauthorizedException extends ApplicationException {
    public static final int DEFAULT_UNAUTHORIZED_EXCEPTION_STATUS_CODE = SipaBootCommonConstants.Http.UNAUTHORIZED;

    public static final int DEFAULT_UNAUTHORIZED_EXCEPTION_NUMERIC_ERROR_CODE = 10006;

    public static final String DEFAULT_UNAUTHORIZED_EXCEPTION_ERROR_CODE = "errors.com.sipa.boot.unauthorized";

    public static final String DEFAULT_UNAUTHORIZED_EXCEPTION_ERROR_MESSAGE = "Unauthorized.";

    public UnauthorizedException() {
        this(DEFAULT_UNAUTHORIZED_EXCEPTION_ERROR_MESSAGE);
    }

    public UnauthorizedException(String pattern, Object... args) {
        this(DEFAULT_UNAUTHORIZED_EXCEPTION_NUMERIC_ERROR_CODE, DEFAULT_UNAUTHORIZED_EXCEPTION_ERROR_CODE, pattern,
            args);
    }

    public UnauthorizedException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(DEFAULT_UNAUTHORIZED_EXCEPTION_STATUS_CODE, numericErrCode,
            Optional.ofNullable(errCode).orElse(DEFAULT_UNAUTHORIZED_EXCEPTION_ERROR_CODE), pattern, args);
    }
}
