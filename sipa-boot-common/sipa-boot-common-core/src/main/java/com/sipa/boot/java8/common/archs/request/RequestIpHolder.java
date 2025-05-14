package com.sipa.boot.java8.common.archs.request;

import org.slf4j.MDC;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public abstract class RequestIpHolder {
    public static final String X_REQUEST_IP = "X-Request-IP";

    private static final ThreadLocal<String> REQUEST_IP_HOLDER = new ThreadLocal<>();

    public static String get() {
        return REQUEST_IP_HOLDER.get();
    }

    public static void set(String clientId) {
        REQUEST_IP_HOLDER.set(clientId);

        MDC.put(X_REQUEST_IP, clientId);
    }

    public static String remove() {
        final String id = REQUEST_IP_HOLDER.get();

        REQUEST_IP_HOLDER.remove();

        MDC.remove(X_REQUEST_IP);

        return id;
    }
}
