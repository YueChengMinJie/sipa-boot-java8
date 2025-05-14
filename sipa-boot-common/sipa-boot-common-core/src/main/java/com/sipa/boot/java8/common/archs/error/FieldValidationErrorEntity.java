package com.sipa.boot.java8.common.archs.error;

import java.util.Collections;
import java.util.Map;

import com.sipa.boot.java8.common.archs.error.base.AbstractEntityObject;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public class FieldValidationErrorEntity extends AbstractEntityObject {
    private static final long serialVersionUID = 1L;

    private String fieldName;

    private String errorMessage;

    private String errorCode;

    private String invalidValue;

    private Map<String, Object> messageVars;

    public FieldValidationErrorEntity() {
    }

    public FieldValidationErrorEntity(String fldName, String errMsg, String errCode, String invalidVal,
        Map<String, Object> msgVars) {
        this.fieldName = fldName;
        this.errorMessage = errMsg;
        this.errorCode = errCode;
        this.invalidValue = invalidVal;
        this.messageVars = msgVars;
        if (messageVars == null) {
            this.messageVars = Collections.emptyMap();
        }
    }

    /**
     * The code for the error. This is intended for display to end users when the error message has been translated so
     * that a CSR can send devs error information.
     *
     * @return The error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * The name of the field that produced the validation error.
     *
     * @return The name of the validation failing field.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * The validation error message for the failing fields.
     *
     * @return The validation error message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * The value of the field that caused the validation error
     *
     * @return The invalid field value.
     */
    public String getInvalidValue() {
        return invalidValue;
    }

    /**
     * The map of message variables used to populate the error message.
     *
     * @return The map of message variables.
     */
    public Map<String, Object> getMessageVars() {
        return messageVars;
    }
}
