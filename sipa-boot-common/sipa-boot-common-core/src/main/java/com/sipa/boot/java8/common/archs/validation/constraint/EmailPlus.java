package com.sipa.boot.java8.common.archs.validation.constraint;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.sipa.boot.java8.common.archs.validation.validator.EmailPlusValidator;

/**
 * Check if the value is a valid e-mail address. The check is performed based on a regular expression.
 * <p>
 * <br>
 * <br>
 * <b>Note:</b> This constraint is also satisfied when the value to validate is null, therefore you might also need to
 * specified @NotNull
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Constraint(validatedBy = {EmailPlusValidator.class})
public @interface EmailPlus {
    String message() default "{com.sipa.boot.java8.common.validation.constraint.EmailPlus.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        EmailPlus[] value();
    }
}
