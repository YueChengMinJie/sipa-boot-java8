package com.sipa.boot.java8.common.mvc.filter.header;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author caszhou
 * @date 2021/7/28
 */
public class KeepaliveTimeoutFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        // response.addHeader("Connection", "close");
        filterChain.doFilter(request, response);
    }
}
