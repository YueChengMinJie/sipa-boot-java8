package com.sipa.boot.java8.common.common.exception.advice.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.metadata.ConstraintDescriptor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sipa.boot.java8.common.archs.error.FieldValidationErrorEntity;
import com.sipa.boot.java8.common.archs.error.ValidationErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;
import com.sipa.boot.java8.common.exceptions.ValidationException;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface ConstraintViolationExceptionAdviceTrait extends ExceptionAdviceTrait {
    /**
     * handle java bean validation exception
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    @ExceptionHandler(ConstraintViolationException.class)
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final ConstraintViolationException exception) {
        log(Level.FINEST, exception);

        ValidationException ex = toValidationException(exception);
        return handle(request, response, ResponseEntity.status(ex.getStatusCode()).body(new ValidationErrorEntity(ex)));
    }

    /**
     * create ValidationException
     *
     * @param exception
     *            ConstraintViolationException
     * @return ValidationException
     */
    default ValidationException toValidationException(ConstraintViolationException exception) {
        Map<String, FieldValidationErrorEntity> validationMap =
            new HashMap<>(exception.getConstraintViolations().size());
        for (ConstraintViolation violation : exception.getConstraintViolations()) {
            /*
             * may need to fix filed name later - currently the filed name will be like [method name].[method argument
             * index].[field name] e.g. createAccount.arg0.password if validated by JSR303
             */
            String fieldName = violation.getPropertyPath().toString();
            ConstraintDescriptor<?> descriptor = violation.getConstraintDescriptor();
            FieldValidationErrorEntity failure = new FieldValidationErrorEntity(fieldName, violation.getMessage(),
                descriptor.getAnnotation().annotationType().getName(), String.valueOf(violation.getInvalidValue()),
                null);
            validationMap.put(fieldName, failure);
        }

        return new ValidationException(validationMap);
    }
}
