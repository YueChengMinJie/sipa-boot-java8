package com.sipa.boot.java8.iot.core.exception;

import com.sipa.boot.java8.iot.core.enumerate.EErrorCode;
import com.sipa.boot.java8.iot.core.i18n.LocaleUtils;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class DeviceOperationException extends RuntimeException {
    private final EErrorCode code;

    private final String message;

    public DeviceOperationException(EErrorCode errorCode) {
        this(errorCode, errorCode.getText());
    }

    public DeviceOperationException(EErrorCode errorCode, Throwable cause) {
        super(cause);
        this.code = errorCode;
        this.message = cause.getMessage();
    }

    public DeviceOperationException(EErrorCode code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message == null ? code.getText() : message;
    }

    @Override
    public String getLocalizedMessage() {
        return LocaleUtils.resolveMessage(getMessage());
    }

    public EErrorCode getCode() {
        return code;
    }
}
