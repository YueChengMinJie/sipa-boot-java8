package com.sipa.boot.java8.common.common.exception.advice.jackson;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author caszhou
 * @date 2021/10/29
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public interface JacksonExceptionAdviceTrait extends JsonMappingExceptionAdviceTrait, JsonParseExceptionAdviceTrait {
    /**
     * isSupported
     *
     * @param error
     *            error
     * @return isSupported
     */
    @Override
    default boolean isSupported(Throwable error) {
        return error instanceof JsonMappingException || error instanceof JsonParseException;
    }
}
