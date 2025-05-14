package com.sipa.boot.java8.common.archs.request;

import org.slf4j.MDC;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public class TenantIdHolder {
    public static final String X_TENANT_ID = SipaBootCommonConstants.SIPA_BOOT_TENANT_ID_HEADER;

    private static final ThreadLocal<String> TENANT_ID_HOLDER = new ThreadLocal<>();

    public static String get() {
        return TENANT_ID_HOLDER.get();
    }

    public static void set(String tenantId) {
        TENANT_ID_HOLDER.set(tenantId);

        MDC.put(X_TENANT_ID, tenantId);
    }

    public static String remove() {
        final String tenantId = TENANT_ID_HOLDER.get();

        TENANT_ID_HOLDER.remove();

        MDC.remove(X_TENANT_ID);

        return tenantId;
    }
}
