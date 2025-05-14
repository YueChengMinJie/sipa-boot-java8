package com.sipa.boot.java8.common.api.annotation;

import java.lang.annotation.*;

/**
 * 接受参数是否需要解密
 *
 * @author rstyro
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Decode {
    // noop
}
