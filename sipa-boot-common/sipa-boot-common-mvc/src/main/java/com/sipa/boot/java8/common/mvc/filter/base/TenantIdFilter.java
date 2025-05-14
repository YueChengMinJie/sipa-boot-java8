package com.sipa.boot.java8.common.mvc.filter.base;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sipa.boot.java8.common.archs.request.TenantIdHolder;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public class TenantIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String tenantId = request.getHeader(TenantIdHolder.X_TENANT_ID);
        if (StringUtils.isNotBlank(tenantId)) {
            TenantIdHolder.set(tenantId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantIdHolder.remove();
        }
    }
}
