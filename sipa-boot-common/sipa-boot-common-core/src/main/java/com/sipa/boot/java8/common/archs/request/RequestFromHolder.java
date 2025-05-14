package com.sipa.boot.java8.common.archs.request;

import org.slf4j.MDC;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public abstract class RequestFromHolder {
    public static final String X_REQUEST_FROM = "X-Request-From";

    private static final ThreadLocal<String> REQUEST_FROM_HOLDER = new ThreadLocal<>();

    public static String get() {
        return REQUEST_FROM_HOLDER.get();
    }

    public static void set(String from) {
        REQUEST_FROM_HOLDER.set(from);

        MDC.put(X_REQUEST_FROM, from);
    }

    public static String remove() {
        final String from = REQUEST_FROM_HOLDER.get();

        REQUEST_FROM_HOLDER.remove();

        MDC.remove(X_REQUEST_FROM);

        return from;
    }
}
