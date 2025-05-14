package com.sipa.boot.java8.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 谨用！
 *
 * @author zhouxiajie
 * @date 2020/8/7
 */
@Component
public class EnvUtils {
    private static final String LOCAL = "local";

    private static final String DEV = "dev";

    private static final String QA = "qa";

    private static final String UAT = "uat";

    private static final String REL = "rel";

    @Value("${current.apollo.env:}")
    private static String env;

    public static boolean isLocal() {
        return LOCAL.equalsIgnoreCase(env);
    }

    public static boolean isDev() {
        return DEV.equalsIgnoreCase(env);
    }

    public static boolean isQa() {
        return QA.equalsIgnoreCase(env);
    }

    public static boolean isUat() {
        return UAT.equalsIgnoreCase(env);
    }

    public static boolean isRel() {
        return REL.equalsIgnoreCase(env);
    }
}
