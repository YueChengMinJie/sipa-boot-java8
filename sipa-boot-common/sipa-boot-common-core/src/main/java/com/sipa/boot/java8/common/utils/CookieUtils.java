package com.sipa.boot.java8.common.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author zhouxiajie
 * @date 2021/5/19
 */
public class CookieUtils {
    public static List<String> getCookieValuesByName(Cookie[] cookies, String name) {
        if (ArrayUtils.isNotEmpty(cookies)) {
            return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .collect(Collectors.toList());
        }
        return null;
    }
}
