package com.sipa.boot.java8.common.common.exception.advice.general;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author caszhou
 * @date 2021/10/29
 */
@Order
@ControllerAdvice
public interface GeneralExceptionAdviceTrait extends UnknownExceptionAdviceTrait, ApplicationExceptionAdviceTrait,
    UnsupportedOperationExceptionAdviceTrait, ThrowableAdviceTrait {}
