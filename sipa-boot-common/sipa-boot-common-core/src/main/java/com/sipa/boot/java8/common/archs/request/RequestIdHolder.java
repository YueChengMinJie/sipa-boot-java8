package com.sipa.boot.java8.common.archs.request;

import org.slf4j.MDC;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public abstract class RequestIdHolder {
    public static final String X_REQUEST_ID = "X-Request-Id";

    private static final ThreadLocal<String> REQUEST_ID_HOLDER = new ThreadLocal<>();

    public static String get() {
        return REQUEST_ID_HOLDER.get();
    }

    public static void set(String requestId) {
        REQUEST_ID_HOLDER.set(requestId);

        MDC.put(X_REQUEST_ID, requestId);
    }

    public static String remove() {
        final String requestId = REQUEST_ID_HOLDER.get();

        REQUEST_ID_HOLDER.remove();

        MDC.remove(X_REQUEST_ID);

        return requestId;
    }
}
