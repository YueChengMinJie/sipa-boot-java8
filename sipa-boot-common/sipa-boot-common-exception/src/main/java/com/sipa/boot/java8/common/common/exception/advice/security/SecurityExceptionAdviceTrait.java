package com.sipa.boot.java8.common.common.exception.advice.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author caszhou
 * @date 2021/10/29
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public interface SecurityExceptionAdviceTrait
    extends AuthenticationExceptionAdviceTrait, CsrfExceptionAdviceTrait, AccessDeniedExceptionAdviceTrait {
    /**
     * isSupported
     *
     * @param error
     *            error
     * @return isSupported
     */
    @Override
    default boolean isSupported(Throwable error) {
        return error instanceof AuthenticationException || error instanceof AccessDeniedException;
    }
}
