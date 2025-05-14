package com.sipa.boot.java8.common.api.annotation;

import java.lang.annotation.*;

/**
 * 组合注解，接受解密，返回加密
 *
 * @author rstyro
 */
@Encode
@Decode
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Encrypt {
    // noop
}
