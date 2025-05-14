package com.sipa.boot.java8.common.archs.request;

import org.slf4j.MDC;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public abstract class DeviceIdHolder {
    public static final String X_DEVICE_ID = "X-Device-Id";

    private static final ThreadLocal<String> DEVICE_ID_HOLDER = new ThreadLocal<>();

    public static String get() {
        return DEVICE_ID_HOLDER.get();
    }

    public static void set(String deviceId) {
        DEVICE_ID_HOLDER.set(deviceId);

        MDC.put(X_DEVICE_ID, deviceId);
    }

    public static String remove() {
        final String deviceId = DEVICE_ID_HOLDER.get();

        DEVICE_ID_HOLDER.remove();

        MDC.remove(X_DEVICE_ID);

        return deviceId;
    }
}
