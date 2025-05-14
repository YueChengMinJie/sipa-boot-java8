package com.sipa.boot.java8.common.archs.error.base;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.sipa.boot.java8.common.archs.validation.constraint.EntityObjectValid;

/**
 * @author caszhou
 * @date 2021/10/29
 */
@EntityObjectValid
public abstract class AbstractEntityObject implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String[] DEFAULT_TO_STRING_EXCLUDE_FIELD_NAMES = {"password", "secret", "encodedPassword"};

    public static final String UNSET_STRING = "fc88a77a91c8480bb89669e9d87ccbcc";

    public static final LocalDateTime UNSET_DATETIME = LocalDateTime.of(0, 1, 1, 1, 1);

    public static final LocalDate UNSET_LOCAL_DATE = LocalDate.of(0, 1, 1);

    public static final Long UNSET_LONG = Long.MAX_VALUE;

    public static final Integer UNSET_INT = Integer.MAX_VALUE;

    /**
     * Template method that validates the state of an {@link}. Can be used prior to saving/ updating the {@link}
     *
     * @param context
     *            the ConstraintValidatorContext
     */
    public boolean validate(ConstraintValidatorContext context) {
        return true;
    }

    /**
     * Convenience method for adding a ConstraintViolation to the given context.
     *
     * @param context
     *            the ConstraintValidatorContext
     * @param message
     *            the message to attach to the violation
     */
    protected void violation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message)
            .addConstraintViolation()
            .disableDefaultConstraintViolation();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, DEFAULT_TO_STRING_EXCLUDE_FIELD_NAMES);
    }
}
