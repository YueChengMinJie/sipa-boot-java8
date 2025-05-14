package com.sipa.boot.java8.common.exceptions;

import java.util.Optional;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class UnsupportedMediaTypeException extends ApplicationException {
    public static final int DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_STATUS_CODE =
        SipaBootCommonConstants.Http.UNSUPPORTED_MEDIA_TYPE;

    public static final int DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_NUMERIC_ERROR_CODE = 20004;

    public static final String DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_ERROR_CODE =
        "errors.com.sipa.boot.media_type_unsupported";

    public static final String DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_ERROR_MESSAGE =
        "Your request could not be processed as you are supplying a media type we do not support.";

    public UnsupportedMediaTypeException() {
        this(DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_ERROR_MESSAGE);
    }

    public UnsupportedMediaTypeException(String pattern, Object... args) {
        this(DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_NUMERIC_ERROR_CODE,
            DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_ERROR_CODE, pattern, args);
    }

    public UnsupportedMediaTypeException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_STATUS_CODE, numericErrCode,
            Optional.ofNullable(errCode).orElse(DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_ERROR_CODE), pattern, args);
    }
}
