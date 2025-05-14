package com.sipa.boot.java8.common.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author caszhou
 * @date 2022/3/27
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValueFrom {
    String[] value();
}
