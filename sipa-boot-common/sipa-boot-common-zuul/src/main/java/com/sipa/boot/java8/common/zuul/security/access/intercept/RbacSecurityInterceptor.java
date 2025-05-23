package com.sipa.boot.java8.common.zuul.security.access.intercept;

import java.io.IOException;

import javax.servlet.*;

import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.zuul.security.access.RbacAccessDecisionManager;

/**
 * @author feizhihao
 * @date 2019-07-12 15:14
 */
@Component
public class RbacSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {
    private final FilterInvocationSecurityMetadataSource securityMetadataSource;

    public RbacSecurityInterceptor(FilterInvocationSecurityMetadataSource securityMetadataSource,
        RbacAccessDecisionManager rbacAccessDecisionManager) {
        this.securityMetadataSource = securityMetadataSource;
        super.setAccessDecisionManager(rbacAccessDecisionManager);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);
        invoke(fi);
    }

    public void invoke(FilterInvocation fi) throws IOException, ServletException {
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }
}
