package com.sipa.boot.java8.common.auth.model.token.impl;

import com.sipa.boot.java8.common.auth.model.token.JwtToken;

import io.jsonwebtoken.Claims;

/**
 * @author zhouxiajie
 * @date 2018/2/5
 */
public final class AccessJwtToken implements JwtToken {
    private final String rawToken;

    private Claims claims;

    public AccessJwtToken(final String token, Claims claims) {
        this.rawToken = token;
        this.claims = claims;
    }

    @Override
    public String getToken() {
        return this.rawToken;
    }

    public Claims getClaims() {
        return claims;
    }
}
