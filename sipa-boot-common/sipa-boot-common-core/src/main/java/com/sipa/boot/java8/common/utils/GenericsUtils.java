package com.sipa.boot.java8.common.utils;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

/**
 * @author zhouxiajie
 * @date 2020/8/9
 */
@SuppressWarnings("unchecked")
public class GenericsUtils {
    public static <T> List<T> convert(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return Arrays.asList((T[])list.toArray());
    }

    public static <K, V> Map<K, V> convert(Map<?, ?> map) {
        if (MapUtils.isEmpty(map)) {
            return new HashMap<>();
        }
        return (Map<K, V>)map;
    }
}
