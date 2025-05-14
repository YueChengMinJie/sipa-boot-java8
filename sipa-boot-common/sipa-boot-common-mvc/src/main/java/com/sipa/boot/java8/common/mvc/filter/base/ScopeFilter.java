package com.sipa.boot.java8.common.mvc.filter.base;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sipa.boot.java8.common.archs.request.ScopeHolder;

/**
 * @author feizhihao
 * @date 2020-02-17
 */
public class ScopeFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String scope = request.getHeader(ScopeHolder.X_SCOPE);
        if (StringUtils.isNotBlank(scope)) {
            ScopeHolder.set(scope);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            ScopeHolder.remove();
        }
    }
}
