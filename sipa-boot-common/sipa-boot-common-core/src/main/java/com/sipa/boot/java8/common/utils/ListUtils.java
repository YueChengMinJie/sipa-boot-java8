package com.sipa.boot.java8.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouxiajie
 * @date 2021/6/11
 */
public class ListUtils {
    public static <T> List<T> getAllNullList(int size) {
        List<T> rs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            rs.add(null);
        }
        return rs;
    }

    public static <T> T first(List<T> list) {
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static <T> T second(List<T> list) {
        if (list != null && list.size() > 1) {
            return list.get(1);
        }
        return null;
    }

    public static <T> T last(List<T> list) {
        if (list != null && list.size() > 0) {
            return list.get(list.size() - 1);
        }
        return null;
    }
}
