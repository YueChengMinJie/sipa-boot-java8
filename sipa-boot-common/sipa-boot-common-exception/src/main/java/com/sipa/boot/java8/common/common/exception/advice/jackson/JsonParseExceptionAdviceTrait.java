package com.sipa.boot.java8.common.common.exception.advice.jackson;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.sipa.boot.java8.common.archs.error.ErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;
import com.sipa.boot.java8.common.exceptions.BadRequestException;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface JsonParseExceptionAdviceTrait extends ExceptionAdviceTrait {
    /**
     * handle json parse exception
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    @ExceptionHandler(JsonParseException.class)
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final JsonParseException exception) {
        log(Level.FINEST, exception);

        String errorDetail = "";
        JsonLocation location = exception.getLocation();
        if (location != null) {
            errorDetail = String.format("line: %d; column: %d", location.getLineNr(), location.getColumnNr());
        }

        BadRequestException badRequestException = new BadRequestException(20001, "errors.com.sipa.boot.json_parse_error",
            "Json parse failed with error: {}", errorDetail);

        return handle(request, response,
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorEntity(badRequestException)));
    }
}
