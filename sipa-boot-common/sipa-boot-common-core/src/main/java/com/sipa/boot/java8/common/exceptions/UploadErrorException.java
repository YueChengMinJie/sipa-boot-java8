package com.sipa.boot.java8.common.exceptions;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author jiangaijun
 * @since 2019/11/27 9:58
 */
public class UploadErrorException extends BadRequestException {
    public static final int DEFAULT_BAD_REQUEST_EXCEPTION_STATUS_CODE = SipaBootCommonConstants.Http.BAD_REQUEST;

    public static final int DEFAULT_BAD_REQUEST_EXCEPTION_NUMERIC_ERROR_CODE = 010712;

    public static final String DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_CODE = "errors.com.sipa.boot.common.upload.error";

    public static final String DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_MESSAGE = "upload error";

    public UploadErrorException() {
        this(DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_MESSAGE);
    }

    public UploadErrorException(String pattern, Object... args) {
        this(DEFAULT_BAD_REQUEST_EXCEPTION_NUMERIC_ERROR_CODE, DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_CODE, pattern, args);
    }

    public UploadErrorException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(numericErrCode, errCode, pattern, args);
    }
}
