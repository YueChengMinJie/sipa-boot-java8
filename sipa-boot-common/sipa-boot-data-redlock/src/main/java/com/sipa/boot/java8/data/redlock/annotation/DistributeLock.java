package com.sipa.boot.java8.data.redlock.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xuanyu
 * @date 2019-07-23 11:35
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface DistributeLock {
    String value();

    long waitTime() default 100;

    long leaseTime() default 10;

    TimeUnit unit() default TimeUnit.SECONDS;
}
