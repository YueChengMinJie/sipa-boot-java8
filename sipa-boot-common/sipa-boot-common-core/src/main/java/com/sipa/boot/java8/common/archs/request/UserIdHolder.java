package com.sipa.boot.java8.common.archs.request;

import org.slf4j.MDC;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public class UserIdHolder {
    public static final String X_USER_ID = SipaBootCommonConstants.SIPA_BOOT_USER_ID_HEADER;

    private static final ThreadLocal<String> USER_ID_HOLDER = new ThreadLocal<>();

    public static String get() {
        return USER_ID_HOLDER.get();
    }

    public static void set(String userId) {
        USER_ID_HOLDER.set(userId);

        MDC.put(X_USER_ID, userId);
    }

    public static String remove() {
        final String userId = USER_ID_HOLDER.get();

        USER_ID_HOLDER.remove();

        MDC.remove(X_USER_ID);

        return userId;
    }
}
