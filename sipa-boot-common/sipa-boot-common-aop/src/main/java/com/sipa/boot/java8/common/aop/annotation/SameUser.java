package com.sipa.boot.java8.common.aop.annotation;

import java.lang.annotation.*;

/**
 * @author 甘华根
 * @since 2020/7/31 14:08
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SameUser {
    String module() default "";
}
