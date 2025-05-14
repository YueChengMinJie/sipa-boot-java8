package com.sipa.boot.java8.common.archs.error;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.sipa.boot.java8.common.archs.error.base.AbstractEntityObject;
import com.sipa.boot.java8.common.exceptions.ApplicationException;
import com.sipa.boot.java8.common.utils.AppUtils;

/**
 * An entity used to represent an application exception for end user viewing.
 *
 * @author caszhou
 * @date 2021/10/29
 */
public class ErrorEntity extends AbstractEntityObject {
    private static final long serialVersionUID = 1L;

    private int numericErrorCode;

    private String errorCode;

    private String errorMessage;

    private List<String> messageVars;

    private String originatingService;

    private LocalDateTime timestamp;

    public ErrorEntity() {
        super();

        this.originatingService = AppUtils.getAppName();
        this.numericErrorCode = ApplicationException.DEFAULT_APPLICATION_EXCEPTION_NUMERIC_ERROR_CODE;
        this.errorCode = ApplicationException.DEFAULT_APPLICATION_EXCEPTION_ERROR_CODE;
        this.errorMessage = ApplicationException.DEFAULT_APPLICATION_EXCEPTION_ERROR_MESSAGE;
        this.messageVars = Collections.emptyList();
        this.timestamp = LocalDateTime.now();
    }

    public ErrorEntity(String errMessage) {
        super();

        this.originatingService = AppUtils.getAppName();
        this.numericErrorCode = ApplicationException.DEFAULT_APPLICATION_EXCEPTION_NUMERIC_ERROR_CODE;
        this.errorCode = ApplicationException.DEFAULT_APPLICATION_EXCEPTION_ERROR_CODE;
        this.errorMessage = errMessage;
        this.messageVars = Collections.emptyList();
        this.timestamp = LocalDateTime.now();
    }

    public ErrorEntity(int numericErrCode, String errCode, String errMessage) {
        super();

        this.originatingService = AppUtils.getAppName();
        this.numericErrorCode = numericErrCode;
        this.errorCode = errCode;
        this.errorMessage = errMessage;
        this.messageVars = Collections.emptyList();
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Construct an Error Entity from an ApplicationException instance
     *
     * @param applicationException
     *            The exception from which the ErrorEntity should be built
     */
    public ErrorEntity(ApplicationException applicationException) {
        this.numericErrorCode = applicationException.getNumericErrorCode();
        this.errorCode = applicationException.getErrorCode();
        this.errorMessage = applicationException.getMessage();
        this.messageVars = applicationException.getMessageVars();
        this.originatingService = applicationException.getOriginatingService();
        this.timestamp = applicationException.getTimestamp();
    }

    public int getNumericErrorCode() {
        return numericErrorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<String> getMessageVars() {
        return messageVars;
    }

    public String getOriginatingService() {
        return originatingService;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setNumericErrorCode(int numericErrorCode) {
        this.numericErrorCode = numericErrorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setMessageVars(List<String> messageVars) {
        this.messageVars = messageVars;
    }

    public void setOriginatingService(String originatingService) {
        this.originatingService = originatingService;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
