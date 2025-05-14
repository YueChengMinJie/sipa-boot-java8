package com.sipa.boot.java8.common.archs.request;

import org.slf4j.MDC;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author feizhihao
 * @date 2020-02-17
 */
public class ScopeHolder {
    public static final String X_SCOPE = SipaBootCommonConstants.SIPA_BOOT_SCOPE_HEADER;

    private static final ThreadLocal<String> SCOPE_HOLDER = new ThreadLocal<>();

    public static String get() {
        return SCOPE_HOLDER.get();
    }

    public static void set(String scope) {
        SCOPE_HOLDER.set(scope);

        MDC.put(X_SCOPE, scope);
    }

    public static String remove() {
        final String scope = SCOPE_HOLDER.get();

        SCOPE_HOLDER.remove();

        MDC.remove(X_SCOPE);

        return scope;
    }
}
