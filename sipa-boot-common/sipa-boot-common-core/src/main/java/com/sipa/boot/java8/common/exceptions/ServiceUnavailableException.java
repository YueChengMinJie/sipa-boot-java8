package com.sipa.boot.java8.common.exceptions;

import java.util.Optional;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * An exception thrown to indicate that the server is currently unable to respond to a request, but that it may be able
 * to respond to the request at a later time.
 * <p>
 */
public class ServiceUnavailableException extends ApplicationException {
    public static final int DEFAULT_SERVICE_UNAVAILABLE_EXCEPTION_STATUS_CODE =
        SipaBootCommonConstants.Http.SERVICE_UNAVAILABLE;

    public static final int DEFAULT_SERVICE_UNAVAILABLE_EXCEPTION_NUMERIC_ERROR_CODE = 10005;

    public static final String DEFAULT_SERVICE_UNAVAILABLE_EXCEPTION_ERROR_CODE =
        "errors.com.sipa.boot.service_unavailable";

    public static final String DEFAULT_SERVICE_UNAVAILABLE_EXCEPTION_ERROR_MESSAGE = "Service unavailable.";

    public ServiceUnavailableException() {
        this(DEFAULT_SERVICE_UNAVAILABLE_EXCEPTION_ERROR_MESSAGE);
    }

    public ServiceUnavailableException(String pattern, Object... args) {
        this(DEFAULT_SERVICE_UNAVAILABLE_EXCEPTION_NUMERIC_ERROR_CODE, DEFAULT_SERVICE_UNAVAILABLE_EXCEPTION_ERROR_CODE,
            pattern, args);
    }

    public ServiceUnavailableException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(DEFAULT_SERVICE_UNAVAILABLE_EXCEPTION_STATUS_CODE, numericErrCode,
            Optional.ofNullable(errCode).orElse(DEFAULT_SERVICE_UNAVAILABLE_EXCEPTION_ERROR_CODE), pattern, args);
    }
}
