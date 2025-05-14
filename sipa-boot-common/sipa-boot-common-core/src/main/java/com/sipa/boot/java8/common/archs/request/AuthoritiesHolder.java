package com.sipa.boot.java8.common.archs.request;

import org.slf4j.MDC;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author feizhihao
 * @date 2020-02-17
 */
public class AuthoritiesHolder {
    public static final String X_AUTHORITIES = SipaBootCommonConstants.SIPA_BOOT_AUTHORITIES_HEADER;

    private static final ThreadLocal<String> AUTHORITIES_HOLDER = new ThreadLocal<>();

    public static String get() {
        return AUTHORITIES_HOLDER.get();
    }

    public static void set(String authorities) {
        AUTHORITIES_HOLDER.set(authorities);

        MDC.put(X_AUTHORITIES, authorities);
    }

    public static String remove() {
        final String authorities = AUTHORITIES_HOLDER.get();

        AUTHORITIES_HOLDER.remove();

        MDC.remove(X_AUTHORITIES);

        return authorities;
    }
}
