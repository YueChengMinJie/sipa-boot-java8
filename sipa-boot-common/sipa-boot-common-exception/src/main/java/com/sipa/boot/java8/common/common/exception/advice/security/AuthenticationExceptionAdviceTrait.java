package com.sipa.boot.java8.common.common.exception.advice.security;

import static com.sipa.boot.java8.common.exceptions.UnauthorizedException.DEFAULT_UNAUTHORIZED_EXCEPTION_ERROR_CODE;
import static com.sipa.boot.java8.common.exceptions.UnauthorizedException.DEFAULT_UNAUTHORIZED_EXCEPTION_NUMERIC_ERROR_CODE;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sipa.boot.java8.common.archs.error.ErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface AuthenticationExceptionAdviceTrait extends ExceptionAdviceTrait {
    /**
     * handle Authentication exception
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    @ExceptionHandler(AuthenticationException.class)
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final AuthenticationException exception) {
        log(Level.INFO, exception);

        return handle(request, response,
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorEntity(DEFAULT_UNAUTHORIZED_EXCEPTION_NUMERIC_ERROR_CODE,
                    DEFAULT_UNAUTHORIZED_EXCEPTION_ERROR_CODE, exception.getMessage())));
    }
}
