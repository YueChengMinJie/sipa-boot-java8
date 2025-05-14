package com.sipa.boot.java8.common.common.exception.advice.jackson;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.sipa.boot.java8.common.archs.error.ErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;
import com.sipa.boot.java8.common.exceptions.BadRequestException;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface JsonMappingExceptionAdviceTrait extends ExceptionAdviceTrait {
    /**
     * handle json mapping exception
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    @ExceptionHandler(JsonMappingException.class)
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final JsonMappingException exception) {
        log(Level.FINEST, exception);

        List<String> fields = new ArrayList<>();
        List<JsonMappingException.Reference> references = exception.getPath();
        fields
            .addAll(references.stream().map(JsonMappingException.Reference::getFieldName).collect(Collectors.toList()));

        BadRequestException badRequestException = new BadRequestException(20000, "errors.com.sipa.boot.json_mapping_error",
            "Json mapping failed with error: {}.", fields.toArray());

        return handle(request, response,
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorEntity(badRequestException)));
    }
}
