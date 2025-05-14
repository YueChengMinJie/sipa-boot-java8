package com.sipa.boot.java8.common.auth.model;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author zhouxiajie
 * @date 2018/3/1
 */
public class SignAuthenticationToken extends AbstractAuthenticationToken {
    private String sign;

    private String signKey;

    public SignAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    public SignAuthenticationToken(Collection<? extends GrantedAuthority> authorities, String sign, String signKey) {
        super(authorities);
        this.sign = sign;
        this.signKey = signKey;
    }

    @Override
    public Object getCredentials() {
        return this.sign;
    }

    @Override
    public Object getPrincipal() {
        return this.signKey;
    }
}
