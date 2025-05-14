package com.sipa.boot.java8.iot.core.dict.base;

import java.lang.annotation.*;

/**
 * @author caszhou
 * @date 2021/9/26
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dict {
    String value() default "";

    String alias() default "";

    String comments() default "";
}
