package com.sipa.boot.java8.common.exceptions;

import com.sipa.boot.java8.common.archs.error.UnknownErrorEntity;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class UnknownException extends ApplicationException {
    private String trackingId;

    public UnknownException(UnknownErrorEntity errorEntity) {
        super(DEFAULT_APPLICATION_EXCEPTION_STATUS_CODE, errorEntity.getNumericErrorCode(), errorEntity.getErrorCode(),
            errorEntity.getErrorMessage(), errorEntity.getMessageVars().toArray());

        setTrackingId(errorEntity.getTrackingId());
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }
}
