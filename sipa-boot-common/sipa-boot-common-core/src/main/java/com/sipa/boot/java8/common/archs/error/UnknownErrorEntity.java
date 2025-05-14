package com.sipa.boot.java8.common.archs.error;

import static com.sipa.boot.java8.common.exceptions.ApplicationException.DEFAULT_APPLICATION_EXCEPTION_ERROR_CODE;
import static com.sipa.boot.java8.common.exceptions.ApplicationException.DEFAULT_APPLICATION_EXCEPTION_NUMERIC_ERROR_CODE;

import java.util.UUID;

import com.sipa.boot.java8.common.exceptions.UnknownException;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public class UnknownErrorEntity extends ErrorEntity {
    private static final long serialVersionUID = 1L;

    private String trackingId;

    public UnknownErrorEntity() {
    }

    public UnknownErrorEntity(String errMessage) {
        super(DEFAULT_APPLICATION_EXCEPTION_NUMERIC_ERROR_CODE, DEFAULT_APPLICATION_EXCEPTION_ERROR_CODE, errMessage);

        this.trackingId = UUID.randomUUID().toString();
    }

    public UnknownErrorEntity(UnknownException exception) {
        super(exception.getNumericErrorCode(), exception.getErrorCode(), exception.getMessage());

        setMessageVars(exception.getMessageVars());
        setTrackingId(exception.getTrackingId());
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }
}
