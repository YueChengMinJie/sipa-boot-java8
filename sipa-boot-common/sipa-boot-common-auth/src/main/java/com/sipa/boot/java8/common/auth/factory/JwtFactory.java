package com.sipa.boot.java8.common.auth.factory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.common.auth.model.ETokenScope;
import com.sipa.boot.java8.common.auth.model.UserContext;
import com.sipa.boot.java8.common.auth.model.token.impl.AccessJwtToken;
import com.sipa.boot.java8.common.auth.property.AuthProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author zhouxiajie
 * @date 2018/2/7
 */
@Component
public class JwtFactory {
    private final AuthProperties settings;

    @Autowired
    public JwtFactory(AuthProperties settings) {
        this.settings = settings;
    }

    /**
     * Factory method for issuing new JWT Tokens.
     */
    public AccessJwtToken createAccessJwtToken(UserContext userContext) {
        if (userContext.getUser() == null || StringUtils.isBlank(userContext.getUser().getId())) {
            throw new AuthenticationServiceException("Cannot create JWT Token without user_id");
        }

        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty()) {
            throw new InsufficientAuthenticationException("User doesn't have any privileges");
        }

        Claims claims = Jwts.claims().setSubject(userContext.getUser().getId());
        claims.put("scopes", Collections.singletonList(ETokenScope.ACCESS_TOKEN.scope()));
        claims.put("authorities",
            userContext.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));
        claims.put("userDetail", Collections.singletonList(JSONObject.toJSONString(userContext.getUser())));

        LocalDateTime currentTime = LocalDateTime.now();

        String token = Jwts.builder()
            .setClaims(claims)
            .setIssuer(settings.getTokenIssuer())
            .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
            .setExpiration(Date.from(
                currentTime.plusMinutes(settings.getTokenExpirationTime()).atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
            .compact();

        return new AccessJwtToken(token, claims);
    }

    public AccessJwtToken createRefreshToken(UserContext userContext) {
        if (userContext.getUser() == null || StringUtils.isBlank(userContext.getUser().getId())) {
            throw new AuthenticationServiceException("Cannot create JWT Token without user_id");
        }

        LocalDateTime currentTime = LocalDateTime.now();

        Claims claims = Jwts.claims().setSubject(userContext.getUser().getId());
        claims.put("scopes", Collections.singletonList(ETokenScope.REFRESH_TOKEN.scope()));

        String token = Jwts.builder()
            .setClaims(claims)
            .setIssuer(settings.getTokenIssuer())
            .setId(UUID.randomUUID().toString())
            .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
            .setExpiration(Date.from(
                currentTime.plusMinutes(settings.getRefreshTokenExpTime()).atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
            .compact();

        return new AccessJwtToken(token, claims);
    }
}
