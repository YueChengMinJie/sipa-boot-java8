package com.sipa.boot.java8.common.common.exception.advice.validation;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sipa.boot.java8.common.archs.error.FieldValidationErrorEntity;
import com.sipa.boot.java8.common.archs.error.ValidationErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;
import com.sipa.boot.java8.common.exceptions.ValidationException;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface MethodArgumentNotValidExceptionAdviceTrait extends ExceptionAdviceTrait {
    /**
     * handle Method Argument Not Valid exception
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final MethodArgumentNotValidException exception) {
        log(Level.FINEST, exception);

        final List<FieldValidationErrorEntity> violations =
            Stream
                .concat(exception.getBindingResult().getFieldErrors().stream().map(this::createViolation),
                    exception.getBindingResult().getGlobalErrors().stream().map(this::createViolation))
                .collect(toList());

        // maybe let spring default exception handler to process it
        Map<String, FieldValidationErrorEntity> fields = new HashMap<>(violations.size());

        for (FieldValidationErrorEntity violation : violations) {
            fields.put(violation.getFieldName(), violation);
        }

        ValidationException ex = new ValidationException(fields);
        return handle(request, response, ResponseEntity.status(ex.getStatusCode()).body(new ValidationErrorEntity(ex)));
    }

    /**
     * create FieldValidationFailure
     *
     * @param error
     *            FieldError
     * @return FieldValidationFailure
     */
    default FieldValidationErrorEntity createViolation(final FieldError error) {
        final String fieldName = error.getObjectName() + "." + error.getField();
        return new FieldValidationErrorEntity(fieldName, error.getDefaultMessage(), error.getCode(),
            String.valueOf(error.getRejectedValue()), null);
    }

    /**
     * create FieldValidationFailure
     *
     * @param error
     *            ObjectError
     * @return FieldValidationFailure
     */
    default FieldValidationErrorEntity createViolation(final ObjectError error) {
        final String fieldName = error.getObjectName();
        return new FieldValidationErrorEntity(fieldName, error.getDefaultMessage(), error.getCode(), null, null);
    }
}
