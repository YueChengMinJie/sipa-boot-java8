package com.sipa.boot.java8.common.utils;

import com.sipa.boot.java8.common.log.Log;

/**
 * @author caszhou
 * @date 2021/8/2
 */
public class LogUtils {
    public static void debug(Log logger, String format, Object... arguments) {
        if (logger.isDebugEnabled()) {
            logger.debug(format, arguments);
        }
    }
}
