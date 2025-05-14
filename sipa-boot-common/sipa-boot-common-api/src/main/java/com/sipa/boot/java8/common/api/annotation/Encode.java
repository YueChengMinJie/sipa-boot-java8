package com.sipa.boot.java8.common.api.annotation;

import java.lang.annotation.*;

/**
 * 返回数据是否加密
 *
 * @author rstyro
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Encode {
    // noop
}
