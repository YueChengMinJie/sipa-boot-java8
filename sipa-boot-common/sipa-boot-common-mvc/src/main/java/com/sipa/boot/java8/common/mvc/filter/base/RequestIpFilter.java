package com.sipa.boot.java8.common.mvc.filter.base;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sipa.boot.java8.common.archs.request.RequestIpHolder;

/**
 * @author feizhihao
 * @date 2019-10-12
 */
public class RequestIpFilter extends OncePerRequestFilter {
    private static final String X_REAL_IP = "X-Real-Ip";

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Inspecting request ip from request header");
        }

        String ip = getRequestIp(request);
        if (StringUtils.isNotBlank(ip)) {
            RequestIpHolder.set(ip);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            RequestIpHolder.remove();
        }
    }

    private String getRequestIp(HttpServletRequest request) {
        if (request != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Looking at request to determine ip address");
            }
            String remoteAddr = request.getHeader(X_REAL_IP);
            if (StringUtils.isBlank(remoteAddr)) {
                List<String> forwardedForHeaders = Collections.list(request.getHeaders(X_FORWARDED_FOR));
                if (logger.isDebugEnabled()) {
                    logger.info("Forwarded for headers: " + forwardedForHeaders);
                }
                for (String forwardedForAddressValue : forwardedForHeaders) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("looking at X-Forwarded-For header value " + forwardedForAddressValue);
                    }
                    String[] forwardedForAddressArr = forwardedForAddressValue.split(",");
                    for (String forwardedForAddress : forwardedForAddressArr) {
                        remoteAddr = StringUtils.trim(forwardedForAddress);
                        return remoteAddr;
                    }
                }
            }
            if (StringUtils.isBlank(remoteAddr)) {
                remoteAddr = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isBlank(remoteAddr) || "unknown".equalsIgnoreCase(remoteAddr)) {
                remoteAddr = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isBlank(remoteAddr) || "unknown".equalsIgnoreCase(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
            String[] ips = remoteAddr.split(",");
            remoteAddr = "0:0:0:0:0:0:0:1".equals(ips[0]) ? "127.0.0.1" : ips[0];
            if (logger.isDebugEnabled()) {
                logger.debug("No valid X-Forwarded-For headers returning request remote address " + remoteAddr);
            }
            return remoteAddr;
        }
        return null;
    }
}
