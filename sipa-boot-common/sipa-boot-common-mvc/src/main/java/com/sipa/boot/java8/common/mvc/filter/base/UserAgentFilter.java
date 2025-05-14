package com.sipa.boot.java8.common.mvc.filter.base;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sipa.boot.java8.common.archs.request.UserAgentHolder;

/**
 * @author feizhihao
 * @date 2019-10-12
 */
public class UserAgentFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String userAgent = request.getHeader(UserAgentHolder.X_USER_AGENT);
        if (StringUtils.isNotBlank(userAgent)) {
            UserAgentHolder.set(userAgent);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserAgentHolder.remove();
        }
    }
}
