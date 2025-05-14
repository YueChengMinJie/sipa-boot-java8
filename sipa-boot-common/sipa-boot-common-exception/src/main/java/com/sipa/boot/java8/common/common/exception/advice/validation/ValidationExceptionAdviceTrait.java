package com.sipa.boot.java8.common.common.exception.advice.validation;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sipa.boot.java8.common.archs.error.ValidationErrorEntity;
import com.sipa.boot.java8.common.exceptions.ValidationException;

/**
 * @author caszhou
 * @date 2021/10/29
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public interface ValidationExceptionAdviceTrait
    extends ConstraintViolationExceptionAdviceTrait, MethodArgumentNotValidExceptionAdviceTrait {
    /**
     * handle validation application exception
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    @ExceptionHandler(ValidationException.class)
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final ValidationException exception) {
        log(Level.FINEST, exception);

        ValidationErrorEntity error = new ValidationErrorEntity(exception);

        return handle(request, response, ResponseEntity.status(exception.getStatusCode()).body(error));
    }
}
