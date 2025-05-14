package com.sipa.boot.java8.common.auth.model;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author zhouxiajie
 * @date 2018/3/1
 */
public class AnonyAuthenticationToken extends AbstractAuthenticationToken {
    private String credential;

    private String principal;

    public AnonyAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    public AnonyAuthenticationToken(Collection<? extends GrantedAuthority> authorities, String credential,
        String principal) {
        super(authorities);
        this.credential = credential;
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return this.credential;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
