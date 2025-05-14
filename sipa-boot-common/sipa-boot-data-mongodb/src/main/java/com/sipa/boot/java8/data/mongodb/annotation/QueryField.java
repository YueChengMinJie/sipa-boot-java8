package com.sipa.boot.java8.data.mongodb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author feizhihao
 * @version 2019-12-16
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryField {
    QueryType type() default QueryType.EQUALS;

    String attribute() default "";
}
