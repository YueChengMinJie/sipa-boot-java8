package com.sipa.boot.java8.iot.core.util;

import com.sipa.boot.java8.iot.core.message.base.IHeaders;
import com.sipa.boot.java8.iot.core.message.base.IMessage;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DeviceMessageUtils {
    public static void trace(IMessage message, String name) {
        trace(message, name, System.currentTimeMillis());
    }

    public static void trace(IMessage message, String name, Object value) {
        if (message.getHeaderOrDefault(IHeaders.enableTrace)) {
            message.addHeader("_trace:" + name, value);
        }
    }
}
