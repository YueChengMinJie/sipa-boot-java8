package com.sipa.boot.java8.common.oauth2.token;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author zhouxiajie
 * @date 2018/3/1
 */
public class AnonyAuthenticationToken extends AbstractAuthenticationToken {
    private String userId;

    private String tenantId;

    public AnonyAuthenticationToken(Collection<? extends GrantedAuthority> authorities, String userId,
        String tenantId) {
        super(authorities);
        this.userId = userId;
        this.tenantId = tenantId;
    }

    @Override
    public Object getCredentials() {
        return this.userId;
    }

    @Override
    public Object getPrincipal() {
        return this.userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
