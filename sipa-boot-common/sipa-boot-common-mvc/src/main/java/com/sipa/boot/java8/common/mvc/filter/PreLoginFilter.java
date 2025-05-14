package com.sipa.boot.java8.common.mvc.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.collect.Lists;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.mvc.context.RequestContextHolder;

/**
 * @author sunyukun
 * @date 2019/2/21
 */
@Component
public class PreLoginFilter extends OncePerRequestFilter {
    private static final Log logger = LogFactory.get(PreLoginFilter.class);

    private final List<String> filterUrlList = Lists.newArrayList("/tenant/pre_login", "/station/pre_login");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String hostName =
            StringUtils.lowerCase(StringUtils.isNotBlank(request.getServerName()) ? request.getServerName() : "");
        RequestContextHolder.setHostName(hostName);
        filterChain.doFilter(request, response);
        RequestContextHolder.release();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (filterUrlList.contains(request.getRequestURI())) {
            logger.info("{} request {} {}", StringUtils.repeat("*", 10), request.getRequestURI(),
                StringUtils.repeat("*", 10));
        }
        return !filterUrlList.contains(request.getRequestURI());
    }
}
