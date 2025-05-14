package com.sipa.boot.java8.common.common.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.sipa.boot.java8.common.common.exception.advice.general.GeneralExceptionAdviceTrait;
import com.sipa.boot.java8.common.common.exception.advice.http.HttpExceptionAdviceTrait;
import com.sipa.boot.java8.common.common.exception.advice.io.IOExceptionAdviceTrait;
import com.sipa.boot.java8.common.common.exception.advice.routing.RoutingExceptionAdviceTrait;
import com.sipa.boot.java8.common.common.exception.advice.validation.ValidationExceptionAdviceTrait;

/**
 * @author caszhou
 * @date 2021/10/29
 */
@Order
@ControllerAdvice
public class DefaultWebExceptionHandling implements GeneralExceptionAdviceTrait, HttpExceptionAdviceTrait,
    IOExceptionAdviceTrait, RoutingExceptionAdviceTrait, ValidationExceptionAdviceTrait {
    @Override
    public Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final Throwable exception) {
        return handleThrowable(request, response, exception);
    }

    @Override
    public boolean isSupported(Throwable error) {
        return true;
    }
}
