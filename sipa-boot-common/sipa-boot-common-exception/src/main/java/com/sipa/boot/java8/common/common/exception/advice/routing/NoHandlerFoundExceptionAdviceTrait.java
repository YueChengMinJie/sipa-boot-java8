package com.sipa.boot.java8.common.common.exception.advice.routing;

import static com.sipa.boot.java8.common.exceptions.NotFoundException.DEFAULT_NOT_FOUND_EXCEPTION_ERROR_CODE;
import static com.sipa.boot.java8.common.exceptions.NotFoundException.DEFAULT_NOT_FOUND_EXCEPTION_NUMERIC_ERROR_CODE;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.sipa.boot.java8.common.archs.error.ErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface NoHandlerFoundExceptionAdviceTrait extends ExceptionAdviceTrait {
    /**
     * handle No Handler Found exception
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    @ExceptionHandler({NoHandlerFoundException.class})
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final NoHandlerFoundException exception) {
        log(Level.FINEST, exception);

        request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());

        return handle(request, response,
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorEntity(DEFAULT_NOT_FOUND_EXCEPTION_NUMERIC_ERROR_CODE,
                    DEFAULT_NOT_FOUND_EXCEPTION_ERROR_CODE, exception.getMessage())));
    }
}
