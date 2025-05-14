package com.sipa.boot.java8.common.oauth2.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sipa.boot.java8.common.archs.request.TenantIdHolder;
import com.sipa.boot.java8.common.archs.request.UserIdHolder;
import com.sipa.boot.java8.common.oauth2.token.AnonyAuthenticationToken;

/**
 * @author zhouxiajie
 */
@Deprecated
public class OAuth2Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Utils.class);

    public static String getUserId() {
        String userId = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonyAuthenticationToken) {
            userId = ((AnonyAuthenticationToken)authentication).getUserId();
        }

        if (StringUtils.isBlank(userId)) {
            userId = UserIdHolder.get();
        }

        LOGGER.info("current user id is [{}]", userId);
        return userId;
    }

    public static String getTenantId() {
        String tenantId = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonyAuthenticationToken) {
            tenantId = ((AnonyAuthenticationToken)authentication).getTenantId();
        }

        if (StringUtils.isBlank(tenantId)) {
            tenantId = TenantIdHolder.get();
        }

        LOGGER.info("current tenant id is [{}]", tenantId);
        return tenantId;
    }
}
