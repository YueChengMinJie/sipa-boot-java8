package com.sipa.boot.java8.common.auth.provider;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.common.auth.model.JwtAuthenticationToken;
import com.sipa.boot.java8.common.auth.model.User;
import com.sipa.boot.java8.common.auth.model.UserContext;
import com.sipa.boot.java8.common.auth.model.token.impl.RawAccessToken;
import com.sipa.boot.java8.common.auth.property.AuthProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * @author zhouxiajie
 * @date 2018/2/6
 */
@Component
@SuppressWarnings("unchecked")
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final AuthProperties authProperties;

    @Autowired
    public JwtAuthenticationProvider(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RawAccessToken rawAccessToken = (RawAccessToken)authentication.getCredentials();

        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(authProperties.getTokenSigningKey());

        List<String> authorities = jwsClaims.getBody().get("authorities", List.class);
        List<GrantedAuthority> grantedAuthorities =
            authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        UserContext context = UserContext.create(getUser(jwsClaims), grantedAuthorities);

        return new JwtAuthenticationToken(context, context.getAuthorities());
    }

    private User getUser(Jws<Claims> jwsClaims) {
        List<String> users = (List<String>)jwsClaims.getBody().get("userDetail");
        return JSONObject.parseObject(users.get(0), User.class);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
