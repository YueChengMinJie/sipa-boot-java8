package com.sipa.boot.java8.common.common.exception.advice.general;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sipa.boot.java8.common.archs.error.ErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;
import com.sipa.boot.java8.common.exceptions.ApplicationException;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface ApplicationExceptionAdviceTrait extends ExceptionAdviceTrait {
    /**
     * handle normal application exception
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    @ExceptionHandler(ApplicationException.class)
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final ApplicationException exception) {
        switch (HttpStatus.valueOf(exception.getStatusCode())) {
            case BAD_REQUEST:
            case NOT_FOUND:
            case UNSUPPORTED_MEDIA_TYPE:
                log(Level.FINEST, exception);
                break;
            case UNAUTHORIZED:
            case FORBIDDEN:
            case CONFLICT:
                log(Level.INFO, exception);
                break;
            case INTERNAL_SERVER_ERROR:
            case SERVICE_UNAVAILABLE:
                log(Level.SEVERE, exception);
                break;
            default:
                if (HttpStatus.valueOf(exception.getStatusCode()).is5xxServerError()) {
                    log(Level.SEVERE, exception);
                } else if (HttpStatus.valueOf(exception.getStatusCode()).is4xxClientError()) {
                    log(Level.INFO, exception);
                }
        }

        return handle(request, response,
            ResponseEntity.status(exception.getStatusCode()).body(new ErrorEntity(exception)));
    }
}
