package com.sipa.boot.java8.common.common.exception.advice.routing;

import static com.sipa.boot.java8.common.exceptions.BadRequestException.DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_CODE;
import static com.sipa.boot.java8.common.exceptions.BadRequestException.DEFAULT_BAD_REQUEST_EXCEPTION_NUMERIC_ERROR_CODE;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sipa.boot.java8.common.archs.error.ErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface MissingServletRequestParameterExceptionAdviceTrait extends ExceptionAdviceTrait {
    /**
     * handle Missing Servlet Request Parameter exception
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final MissingServletRequestParameterException exception) {
        log(Level.FINEST, exception);

        return handle(request, response,
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorEntity(DEFAULT_BAD_REQUEST_EXCEPTION_NUMERIC_ERROR_CODE,
                    DEFAULT_BAD_REQUEST_EXCEPTION_ERROR_CODE, exception.getMessage())));
    }
}
