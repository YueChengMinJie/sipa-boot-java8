package com.sipa.boot.java8.common.utils;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhouxiajie
 * @date 2020/9/8
 */
public class CheckUtils {
    public static String requireNonBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalStateException(message);
        }
        return str;
    }

    public static <T> List<T> requireNonNull(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalStateException("List cannot be null");
        }
        return list;
    }

    public static <T> List<T> requireNonNull(List<T> list, String message) {
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalStateException(message);
        }
        return list;
    }
}
