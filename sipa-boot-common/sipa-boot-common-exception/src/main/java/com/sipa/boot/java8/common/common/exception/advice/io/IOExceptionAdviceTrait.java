package com.sipa.boot.java8.common.common.exception.advice.io;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author caszhou
 * @date 2021/10/29
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public interface IOExceptionAdviceTrait extends HttpMessageNotReadableExceptionAdviceTrait,
    MultipartExceptionAdviceTrait, TypeMismatchExceptionAdviceTrait, EofExceptionAdviceTrait {

}
