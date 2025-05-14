package com.sipa.boot.java8.common.exceptions;

import java.time.LocalDateTime;
import java.util.Optional;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.utils.AppUtils;

public abstract class ApplicationException extends AbstractException {
    public static final int DEFAULT_APPLICATION_EXCEPTION_STATUS_CODE =
        SipaBootCommonConstants.Http.INTERNAL_SERVER_ERROR;

    public static final int DEFAULT_APPLICATION_EXCEPTION_NUMERIC_ERROR_CODE = -1;

    public static final String DEFAULT_APPLICATION_EXCEPTION_ERROR_CODE = "errors.com.sipa.boot.unknown_error";

    public static final String DEFAULT_APPLICATION_EXCEPTION_ERROR_MESSAGE =
        "An error occurred and we were unable to resolve it, please contact support on customer service.";

    /**
     * http status code
     */
    private final int statusCode;

    /**
     * numeric error code
     */
    private final int numericErrorCode;

    /**
     * string error code
     */
    private final String errorCode;

    /**
     * originating service
     */
    private String originatingService;

    /**
     * timestamp for exception
     */
    private LocalDateTime timestamp;

    protected ApplicationException() {
        this(DEFAULT_APPLICATION_EXCEPTION_STATUS_CODE, DEFAULT_APPLICATION_EXCEPTION_NUMERIC_ERROR_CODE,
            DEFAULT_APPLICATION_EXCEPTION_ERROR_CODE, DEFAULT_APPLICATION_EXCEPTION_ERROR_MESSAGE);
    }

    public ApplicationException(String pattern, Object... args) {
        this(DEFAULT_APPLICATION_EXCEPTION_STATUS_CODE, DEFAULT_APPLICATION_EXCEPTION_NUMERIC_ERROR_CODE,
            DEFAULT_APPLICATION_EXCEPTION_ERROR_CODE, pattern, args);
    }

    protected ApplicationException(int status, int numericErrCode, String errCode, String pattern, Object... args) {
        super(pattern, args);

        this.statusCode = status;
        this.errorCode = Optional.ofNullable(errCode).orElse(DEFAULT_APPLICATION_EXCEPTION_ERROR_CODE);
        this.numericErrorCode = numericErrCode;

        this.originatingService = AppUtils.getAppName();
        this.timestamp = LocalDateTime.now();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getNumericErrorCode() {
        return numericErrorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getOriginatingService() {
        return originatingService;
    }

    public void setOriginatingService(String originatingService) {
        this.originatingService = originatingService;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
