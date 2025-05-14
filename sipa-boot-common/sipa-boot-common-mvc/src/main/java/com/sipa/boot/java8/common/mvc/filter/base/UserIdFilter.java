package com.sipa.boot.java8.common.mvc.filter.base;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sipa.boot.java8.common.archs.request.UserIdHolder;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public class UserIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String userId = request.getHeader(UserIdHolder.X_USER_ID);
        if (StringUtils.isNotBlank(userId)) {
            UserIdHolder.set(userId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserIdHolder.remove();
        }
    }
}
