package com.sipa.boot.java8.common.common.exception.advice.security;

import com.sipa.boot.java8.common.archs.error.ErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;

import static com.sipa.boot.java8.common.exceptions.ForbiddenException.DEFAULT_FORBIDDEN_EXCEPTION_ERROR_CODE;
import static com.sipa.boot.java8.common.exceptions.ForbiddenException.DEFAULT_FORBIDDEN_EXCEPTION_NUMERIC_ERROR_CODE;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface AccessDeniedExceptionAdviceTrait extends ExceptionAdviceTrait {
    /**
     * handle Access Denied exception
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    @ExceptionHandler(AccessDeniedException.class)
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final AccessDeniedException exception) {
        log(Level.INFO, exception);

        return handle(request, response,
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorEntity(DEFAULT_FORBIDDEN_EXCEPTION_NUMERIC_ERROR_CODE,
                    DEFAULT_FORBIDDEN_EXCEPTION_ERROR_CODE, exception.getMessage())));
    }
}
