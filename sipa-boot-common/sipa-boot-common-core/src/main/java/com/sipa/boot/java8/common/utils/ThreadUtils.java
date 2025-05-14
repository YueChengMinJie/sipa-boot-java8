package com.sipa.boot.java8.common.utils;

/**
 * @author zhouxiajie
 * @date 2019-03-20
 */
public class ThreadUtils {
    public static void sleepQuitly(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {
        }
    }
}
