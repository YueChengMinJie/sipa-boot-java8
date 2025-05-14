package com.sipa.boot.java8.common.archs.error;

import java.util.Map;

import com.sipa.boot.java8.common.exceptions.ValidationException;

/**
 * An entity used to represent a ValidationException for end user viewing.
 *
 * @author caszhou
 * @date 2021/10/29
 */
public class ValidationErrorEntity extends ErrorEntity {
    private static final long serialVersionUID = 1L;

    protected Map<String, FieldValidationErrorEntity> validationFailures;

    public ValidationErrorEntity() {
    }

    /**
     * Create a new ValidationErrorEntity from a ValidationException.
     *
     * @param validationException
     *            The ValidationException instance
     */
    public ValidationErrorEntity(ValidationException validationException) {
        super(validationException);
        validationFailures = validationException.getValidationFailures();
    }

    /**
     * A map of the fields that failed validation to the validation failure for those fields by field name
     *
     * @return A map of FieldValidationFailures
     */
    public Map<String, FieldValidationErrorEntity> getValidationFailures() {
        return validationFailures;
    }

    public void setValidationFailures(Map<String, FieldValidationErrorEntity> validationFailures) {
        this.validationFailures = validationFailures;
    }
}
