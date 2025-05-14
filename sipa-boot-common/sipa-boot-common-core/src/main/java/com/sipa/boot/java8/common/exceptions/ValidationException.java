package com.sipa.boot.java8.common.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.helpers.MessageFormatter;

import com.sipa.boot.java8.common.archs.error.FieldValidationErrorEntity;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * An exception representing a object validation that failed.
 * <p>
 */
public class ValidationException extends ApplicationException {
    public static final int DEFAULT_VALIDATION_EXCEPTION_STATUS_CODE = SipaBootCommonConstants.Http.BAD_REQUEST;

    public static final int DEFAULT_VALIDATION_EXCEPTION_NUMERIC_ERROR_CODE = 10000;

    public static final String DEFAULT_VALIDATION_EXCEPTION_ERROR_CODE = "errors.com.sipa.boot.validation_failed";

    public static final String DEFAULT_VALIDATION_EXCEPTION_ERROR_MESSAGE = "Validation failed.";

    private final Map<String, FieldValidationErrorEntity> validationFailures = new HashMap<>();

    /**
     * Create a new ValidationException from a Map of FieldValidationFailures
     *
     * @param fieldValidationFailures
     *            The invalid fields triggering this exception.
     */
    public ValidationException(Map<String, FieldValidationErrorEntity> fieldValidationFailures) {
        super(DEFAULT_VALIDATION_EXCEPTION_STATUS_CODE, DEFAULT_VALIDATION_EXCEPTION_NUMERIC_ERROR_CODE,
            DEFAULT_VALIDATION_EXCEPTION_ERROR_CODE, DEFAULT_VALIDATION_EXCEPTION_ERROR_MESSAGE);
        if (fieldValidationFailures != null) {
            this.validationFailures.putAll(fieldValidationFailures);
        }
    }

    public Map<String, FieldValidationErrorEntity> getValidationFailures() {
        return validationFailures;
    }

    @Override
    public String toString() {
        String message = super.toString();
        ArrayList<String> list = new ArrayList<>(this.validationFailures.size());
        list.addAll(validationFailures.values()
            .stream()
            .map(FieldValidationErrorEntity::getErrorMessage)
            .collect(Collectors.toList()));
        message = MessageFormatter.arrayFormat(message + " {}", new Object[] {list}).getMessage();
        return message;
    }
}
