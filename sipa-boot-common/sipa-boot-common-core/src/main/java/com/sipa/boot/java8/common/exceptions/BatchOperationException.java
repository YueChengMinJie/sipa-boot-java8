package com.sipa.boot.java8.common.exceptions;

import java.util.Optional;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * An exception representing a batch operation that failed.
 * <p>
 * And the batch operation error response entity also need to extend from ErrorEntity
 * <p>
 */
public class BatchOperationException extends ApplicationException {
    public static final int DEFAULT_BATCH_OPERATION_EXCEPTION_STATUS_CODE = SipaBootCommonConstants.Http.MULTI_STATUS;

    public static final int DEFAULT_BATCH_OPERATION_EXCEPTION_NUMERIC_ERROR_CODE = 10011;

    public static final String DEFAULT_BATCH_OPERATION_EXCEPTION_ERROR_CODE =
        "errors.com.sipa.boot.batch_operation_failed";

    public static final String DEFAULT_BATCH_OPERATION_EXCEPTION_ERROR_MESSAGE = "Batch operation failed.";

    // add your own success/failure response object in here

    public BatchOperationException() {
        this(DEFAULT_BATCH_OPERATION_EXCEPTION_ERROR_MESSAGE);
    }

    public BatchOperationException(String pattern, Object... args) {
        this(DEFAULT_BATCH_OPERATION_EXCEPTION_NUMERIC_ERROR_CODE, DEFAULT_BATCH_OPERATION_EXCEPTION_ERROR_CODE,
            pattern, args);
    }

    public BatchOperationException(int numericErrCode, String errCode, String pattern, Object... args) {
        super(DEFAULT_BATCH_OPERATION_EXCEPTION_STATUS_CODE, numericErrCode,
            Optional.ofNullable(errCode).orElse(DEFAULT_BATCH_OPERATION_EXCEPTION_ERROR_CODE), pattern, args);
    }
}
