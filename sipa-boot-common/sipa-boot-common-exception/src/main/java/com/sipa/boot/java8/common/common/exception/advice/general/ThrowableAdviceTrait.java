package com.sipa.boot.java8.common.common.exception.advice.general;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sipa.boot.java8.common.archs.error.UnknownErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;
import com.sipa.boot.java8.common.exceptions.ApplicationException;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface ThrowableAdviceTrait extends ExceptionAdviceTrait {
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
    @ExceptionHandler(Throwable.class)
    default Object handleThrowable(final HttpServletRequest request, final HttpServletResponse response,
        final Throwable exception) {
        String errorMessage = ApplicationException.DEFAULT_APPLICATION_EXCEPTION_ERROR_MESSAGE
            + (getExceptionHandlingProperties().isShowUnknownExceptionStack()
                ? "\n\n" + ExceptionUtils.getStackTrace(exception) : "");

        UnknownErrorEntity errorEntity = new UnknownErrorEntity(errorMessage);

        /*
         * Writing error stacktrace to logs with tracking id next to it
         */
        LOGGER.error("Unhandled error during request processing (tracking id: [{}]):", errorEntity.getTrackingId(),
            exception);

        return handle(request, response, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorEntity));
    }
}
