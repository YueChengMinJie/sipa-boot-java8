package com.sipa.boot.java8.common.aop.annotation;

import java.lang.annotation.*;

/**
 * @author zhouxiajie
 * @date 2019-08-02
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrintTime {

}
