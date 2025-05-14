package com.sipa.boot.java8.common.mvc.filter.base;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sipa.boot.java8.common.archs.request.ClientIdHolder;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public class ClientIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String clientId = request.getHeader(ClientIdHolder.X_CLIENT_ID);
        if (StringUtils.isNotBlank(clientId)) {
            ClientIdHolder.set(clientId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            ClientIdHolder.remove();
        }
    }
}
