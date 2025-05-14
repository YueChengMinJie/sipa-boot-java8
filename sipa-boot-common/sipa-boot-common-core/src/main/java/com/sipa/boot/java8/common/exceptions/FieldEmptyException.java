package com.sipa.boot.java8.common.exceptions;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author jiangaijun
 * @since 2019/11/22 14:35
 */
public class FieldEmptyException extends BadRequestException {
    public static final int DEFAULT_BAD_REQUEST_EXCEPTION_STATUS_CODE = SipaBootCommonConstants.Http.BAD_REQUEST;

    public static final int DEFAULT_BAD_REQUEST_EXCEPTION_NUMERIC_ERROR_CODE = 010702;

    public static final String DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_CODE = "errors.com.sipa.boot.common.field.empty.error";

    public static final String DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_MESSAGE = "Bad request.";

    public FieldEmptyException() {
        this(DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_MESSAGE);
    }

    public FieldEmptyException(String pattern, Object... args) {
        this(DEFAULT_BAD_REQUEST_EXCEPTION_NUMERIC_ERROR_CODE, DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_CODE, pattern, args);
    }

    public FieldEmptyException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(numericErrCode, errCode, pattern, args);
    }
}
