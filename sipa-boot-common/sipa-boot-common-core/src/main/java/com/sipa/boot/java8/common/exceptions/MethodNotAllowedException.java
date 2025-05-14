package com.sipa.boot.java8.common.exceptions;

import java.util.Optional;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class MethodNotAllowedException extends ApplicationException {
    public static final int DEFAULT_METHOD_NOT_ALLOWED_EXCEPTION_STATUS_CODE =
        SipaBootCommonConstants.Http.METHOD_NOT_ALLOWED;

    public static final int DEFAULT_METHOD_NOT_ALLOWED_EXCEPTION_NUMERIC_ERROR_CODE = 20003;

    public static final String DEFAULT_METHOD_NOT_ALLOWED_EXCEPTION_ERROR_CODE =
        "errors.com.sipa.boot.method_not_allowed";

    public static final String DEFAULT_METHOD_NOT_ALLOWED_EXCEPTION_ERROR_MESSAGE =
        "The resource you were trying to access cannot be accessed with the HTTP method you used.";

    public MethodNotAllowedException() {
        this(DEFAULT_METHOD_NOT_ALLOWED_EXCEPTION_ERROR_MESSAGE);
    }

    public MethodNotAllowedException(String pattern, Object... args) {
        this(DEFAULT_METHOD_NOT_ALLOWED_EXCEPTION_NUMERIC_ERROR_CODE, DEFAULT_METHOD_NOT_ALLOWED_EXCEPTION_ERROR_CODE,
            pattern, args);
    }

    public MethodNotAllowedException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(DEFAULT_METHOD_NOT_ALLOWED_EXCEPTION_STATUS_CODE, numericErrCode,
            Optional.ofNullable(errCode).orElse(DEFAULT_METHOD_NOT_ALLOWED_EXCEPTION_ERROR_CODE), pattern, args);
    }
}
