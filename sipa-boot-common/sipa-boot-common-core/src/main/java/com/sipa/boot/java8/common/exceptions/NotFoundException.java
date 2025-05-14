package com.sipa.boot.java8.common.exceptions;

import java.util.Optional;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * An exception thrown to indicate that an attempt has been made to access an object that does not exist.
 * <p>
 */
public class NotFoundException extends ApplicationException {
    public static final int DEFAULT_NOT_FOUND_EXCEPTION_STATUS_CODE = SipaBootCommonConstants.Http.NOT_FOUND;

    public static final int DEFAULT_NOT_FOUND_EXCEPTION_NUMERIC_ERROR_CODE = 10002;

    public static final String DEFAULT_NOT_FOUND_EXCEPTION_ERROR_CODE = "errors.com.sipa.boot.not_found";

    public static final String DEFAULT_NOT_FOUND_EXCEPTION_ERROR_MESSAGE =
        "The resource you were trying to find could not be found.";

    public NotFoundException() {
        this(DEFAULT_NOT_FOUND_EXCEPTION_ERROR_MESSAGE);
    }

    public NotFoundException(String pattern, Object... args) {
        this(DEFAULT_NOT_FOUND_EXCEPTION_NUMERIC_ERROR_CODE, DEFAULT_NOT_FOUND_EXCEPTION_ERROR_CODE, pattern, args);
    }

    public NotFoundException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(DEFAULT_NOT_FOUND_EXCEPTION_STATUS_CODE, numericErrCode,
            Optional.ofNullable(errCode).orElse(DEFAULT_NOT_FOUND_EXCEPTION_ERROR_CODE), pattern, args);
    }
}
