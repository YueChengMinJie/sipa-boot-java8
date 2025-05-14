package com.sipa.boot.java8.common.utils;

import java.util.zip.CRC32;

/**
 * @author zhouxiajie
 * @date 2019/10/23
 */
public class CrcUtils {
    public static String encode(String plainText) {
        CRC32 c32 = new CRC32();
        c32.update(plainText.getBytes());
        return Long.toHexString(c32.getValue());
    }
}
