package com.sipa.boot.java8.common.exceptions;

import java.util.Optional;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * An exception thrown to indicate that a conflict has occurred (Duplicate data).
 * <p>
 */
public class ConflictException extends ApplicationException {
    public static final int DEFAULT_CONFLICT_EXCEPTION_STATUS_CODE = SipaBootCommonConstants.Http.CONFLICT;

    public static final int DEFAULT_CONFLICT_EXCEPTION_NUMERIC_ERROR_CODE = 10003;

    public static final String DEFAULT_CONFLICT_EXCEPTION_ERROR_CODE = "errors.com.sipa.boot.conflict";

    public static final String DEFAULT_CONFLICT_EXCEPTION_ERROR_MESSAGE = "Met conflict error.";

    public ConflictException() {
        this(DEFAULT_CONFLICT_EXCEPTION_ERROR_MESSAGE);
    }

    public ConflictException(String pattern, Object... args) {
        this(DEFAULT_CONFLICT_EXCEPTION_NUMERIC_ERROR_CODE, DEFAULT_CONFLICT_EXCEPTION_ERROR_CODE, pattern, args);
    }

    public ConflictException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(DEFAULT_CONFLICT_EXCEPTION_STATUS_CODE, numericErrCode,
            Optional.ofNullable(errCode).orElse(DEFAULT_CONFLICT_EXCEPTION_ERROR_CODE), pattern, args);
    }
}
