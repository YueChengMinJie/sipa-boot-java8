package com.sipa.boot.java8.common.common.exception.advice.general;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sipa.boot.java8.common.archs.error.UnknownErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;
import com.sipa.boot.java8.common.exceptions.UnknownException;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface UnknownExceptionAdviceTrait extends ExceptionAdviceTrait {
    /**
     * handle unknown application exception
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    @ExceptionHandler(UnknownException.class)
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final UnknownException exception) {
        log(Level.SEVERE, exception);

        return handle(request, response,
            ResponseEntity.status(exception.getStatusCode()).body(new UnknownErrorEntity(exception)));
    }
}
