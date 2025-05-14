package com.sipa.boot.java8.common.utils;

import java.util.Iterator;
import java.util.Map;

/**
 * @author zhouxiajie
 * @date 2021/6/11
 */
public class MapUtils {
    public static <K, V> Map.Entry<K, V> getHead(Map<K, V> map) {
        return map.entrySet().iterator().next();
    }

    public static <K, V> Map.Entry<K, V> getTail(Map<K, V> map) {
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> tail = null;
        while (iterator.hasNext()) {
            tail = iterator.next();
        }
        return tail;
    }
}
