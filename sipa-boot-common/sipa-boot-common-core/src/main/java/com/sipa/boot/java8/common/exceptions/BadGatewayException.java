package com.sipa.boot.java8.common.exceptions;

import java.util.Optional;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class BadGatewayException extends ApplicationException {
    public static final int DEFAULT_BAD_GATEWAY_EXCEPTION_STATUS_CODE = SipaBootCommonConstants.Http.BAD_GATEWAY;

    public static final int DEFAULT_BAD_GATEWAY_EXCEPTION_NUMERIC_ERROR_CODE = 10009;

    public static final String DEFAULT_BAD_GATEWAY_EXCEPTION_ERROR_CODE = "errors.com.sipa.boot.bad_gateway";

    public static final String DEFAULT_BAD_GATEWAY_EXCEPTION_ERROR_MESSAGE = "Bad gateway.";

    public BadGatewayException() {
        this(DEFAULT_BAD_GATEWAY_EXCEPTION_ERROR_MESSAGE);
    }

    public BadGatewayException(String pattern, Object... args) {
        this(DEFAULT_BAD_GATEWAY_EXCEPTION_NUMERIC_ERROR_CODE, DEFAULT_BAD_GATEWAY_EXCEPTION_ERROR_CODE, pattern, args);
    }

    public BadGatewayException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(DEFAULT_BAD_GATEWAY_EXCEPTION_STATUS_CODE, numericErrCode,
            Optional.ofNullable(errCode).orElse(DEFAULT_BAD_GATEWAY_EXCEPTION_ERROR_CODE), pattern, args);
    }
}
