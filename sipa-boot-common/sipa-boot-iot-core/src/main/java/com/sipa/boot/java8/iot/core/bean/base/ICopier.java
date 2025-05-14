package com.sipa.boot.java8.iot.core.bean.base;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.sipa.boot.java8.iot.core.bean.FastBeanCopier;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public interface ICopier {
    void copy(Object source, Object target, Set<String> ignore, IConverter converter);

    default void copy(Object source, Object target, String... ignore) {
        copy(source, target, new HashSet<>(Arrays.asList(ignore)), FastBeanCopier.DEFAULT_CONVERT);
    }
}
