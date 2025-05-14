package com.sipa.boot.java8.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipa.boot.java8.common.archs.request.*;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author zhouxiajie
 */
public class AuthUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUtils.class);

    public static String getUserId() {
        String userId = UserIdHolder.get();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("current user id is [{}]", userId);
        }
        return userId;
    }

    public static String getTenantId() {
        String tenantId = TenantIdHolder.get();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("current tenant id is [{}]", tenantId);
        }
        // 对于私有化，没有租户的情况下给个默认值
        return StringUtils.isBlank(tenantId) ? SipaBootCommonConstants.StringValue.STRING_VALUE_0 : tenantId;
    }

    public static String getScope() {
        String scope = ScopeHolder.get();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("current scope is [{}]", scope);
        }
        return scope;
    }

    public static String getClientId() {
        String clientId = ClientIdHolder.get();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("current client id is [{}]", clientId);
        }
        return clientId;
    }

    public static String getAuthorities() {
        String authorities = AuthoritiesHolder.get();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("current authorities is [{}]", authorities);
        }
        return authorities;
    }
}
