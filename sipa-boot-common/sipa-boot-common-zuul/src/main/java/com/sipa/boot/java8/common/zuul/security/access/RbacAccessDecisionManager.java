package com.sipa.boot.java8.common.zuul.security.access;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.LogUtils;
import com.sipa.boot.java8.common.zuul.common.ZuulConstants;

/**
 * @author feizhihao
 * @date 2019-07-12 14:46
 */
@Component
public class RbacAccessDecisionManager implements AccessDecisionManager {
    public static final Log LOGGER = LogFactory.get(RbacAccessDecisionManager.class);

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) {
        LogUtils.debug(LOGGER, "Current user [{}], request url roles [{}]", authentication.getName(), configAttributes);

        if (CollectionUtils.isEmpty(configAttributes)) {
            LogUtils.debug(LOGGER, "No roles, pass all");
            return;
        }

        if (configAttributes.stream()
            .anyMatch(configAttribute -> StringUtils.equals(ZuulConstants.SECURITY_PASS_ROLE,
                configAttribute.getAttribute()))) {
            return;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            if (SipaBootCommonConstants.SIPA_BOOT_SUPER_ADMIN_ROLE.equals(grantedAuthority.getAuthority())) {
                LogUtils.debug(LOGGER, "Find super admin role, pass all");
                return;
            }
        }

        LogUtils.debug(LOGGER, "Current user has roles [{}]", authorities);
        for (ConfigAttribute ca : configAttributes) {
            String needRole = ca.getAttribute().trim();
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                if (needRole.equals(ga.getAuthority().trim())) {
                    LogUtils.debug(LOGGER, "Find need role [{}]", needRole);
                    return;
                }
            }
        }

        throw new AccessDeniedException("No match roles.");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
