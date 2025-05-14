package com.sipa.boot.java8.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author feizhihao
 * @date 2019-05-30 10:37
 */
public class RegexUtils {
    /**
     * 正则匹配
     *
     * @param pattern
     *            正则表达式
     * @param str
     *            验证字符串
     * @return Boolean
     */
    public static Boolean mathPattern(String pattern, String str) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        return m.matches();
    }

    /**
     * 匹配url
     */
    public static Boolean matchUrl(String str) {
        String pattern = "^(https?)://[^\\s]*";
        return mathPattern(pattern, str);
    }
}
