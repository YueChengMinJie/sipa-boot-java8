package com.sipa.boot.java8.common.auth.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author zhouxiajie
 * @date 2018/2/5
 */
public class UserContext {
    private final User user;

    private final List<GrantedAuthority> authorities;

    private UserContext(User user, List<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public static UserContext create(User user, List<GrantedAuthority> authorities) {
        if (user == null || StringUtils.isBlank(user.getId())) {
            throw new AuthenticationServiceException("User object or id is null: " + user);
        }
        return new UserContext(user, authorities);
    }

    public User getUser() {
        return user;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
