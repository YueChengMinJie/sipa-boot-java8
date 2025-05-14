package com.sipa.boot.java8.common.archs.validation.constraint;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.sipa.boot.java8.common.archs.validation.validator.EntityObjectValidator;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE})
@Constraint(validatedBy = EntityObjectValidator.class)
public @interface EntityObjectValid {
    String message() default "{com.sipa.boot.java8.common.validation.constraint.EntityObjectValid.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
