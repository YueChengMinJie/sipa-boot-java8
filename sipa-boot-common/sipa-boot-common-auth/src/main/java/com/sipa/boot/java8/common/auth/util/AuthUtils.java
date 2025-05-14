package com.sipa.boot.java8.common.auth.util;

import com.sipa.boot.java8.common.auth.model.JwtAuthenticationToken;
import com.sipa.boot.java8.common.auth.model.UserContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author zhouxiajie
 * @since 2017/12/1 23:33
 */
public class AuthUtils {
    public static String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            return ((UserContext)authentication.getPrincipal()).getUser().getId();
        }
        return null;
    }

    public static Integer getUserType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            return ((UserContext)authentication.getPrincipal()).getUser().getType();
        }
        return null;
    }

    public static UserContext getUserContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            return ((UserContext)authentication.getPrincipal());
        }
        return null;
    }
}
