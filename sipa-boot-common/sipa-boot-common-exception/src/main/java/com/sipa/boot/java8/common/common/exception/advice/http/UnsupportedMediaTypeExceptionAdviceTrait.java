package com.sipa.boot.java8.common.common.exception.advice.http;

import static com.sipa.boot.java8.common.exceptions.UnsupportedMediaTypeException.DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_ERROR_CODE;
import static com.sipa.boot.java8.common.exceptions.UnsupportedMediaTypeException.DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_NUMERIC_ERROR_CODE;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sipa.boot.java8.common.archs.error.ErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface UnsupportedMediaTypeExceptionAdviceTrait extends ExceptionAdviceTrait {
    /**
     * handle Unsupported MediaType exception
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final HttpMediaTypeNotSupportedException exception) {
        log(Level.FINEST, exception);

        return handle(request, response,
            ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorEntity(DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_NUMERIC_ERROR_CODE,
                    DEFAULT_UNSUPPORTED_MEDIA_TYPE_EXCEPTION_ERROR_CODE, exception.getMessage())));
    }
}
