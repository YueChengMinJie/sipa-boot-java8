package com.sipa.boot.java8.common.archs.request;

import org.slf4j.MDC;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public abstract class ClientIdHolder {
    public static final String X_CLIENT_ID = "X-Client-Id";

    private static final ThreadLocal<String> CLIENT_ID_HOLDER = new ThreadLocal<>();

    public static String get() {
        return CLIENT_ID_HOLDER.get();
    }

    public static void set(String clientId) {
        CLIENT_ID_HOLDER.set(clientId);

        MDC.put(X_CLIENT_ID, clientId);
    }

    public static String remove() {
        final String id = CLIENT_ID_HOLDER.get();

        CLIENT_ID_HOLDER.remove();

        MDC.remove(X_CLIENT_ID);

        return id;
    }
}
