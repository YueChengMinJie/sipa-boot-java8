package com.sipa.boot.java8.common.utils;

import java.util.UUID;

/**
 * @author feizhihao
 * @date 2019-06-05 09:43
 */
public class UuidUtils {
    public static String generator() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
