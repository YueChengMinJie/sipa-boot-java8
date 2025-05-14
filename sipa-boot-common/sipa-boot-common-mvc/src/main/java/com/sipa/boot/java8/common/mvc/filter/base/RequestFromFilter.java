package com.sipa.boot.java8.common.mvc.filter.base;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sipa.boot.java8.common.archs.request.RequestFromHolder;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public class RequestFromFilter extends OncePerRequestFilter {
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String requestFrom = String.join(",", Collections.list(request.getHeaders(RequestFromHolder.X_REQUEST_FROM)));
        if (StringUtils.isBlank(requestFrom) && !CorsUtils.isPreFlightRequest(request)) {
            String reqIp = getRequestIp(request);
            if (StringUtils.isNotBlank(reqIp) && logger.isTraceEnabled()) {
                logger
                    .trace("Header of " + RequestFromHolder.X_REQUEST_FROM + " is not found of request from " + reqIp);
            }
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Found request from the product or service id: " + requestFrom);
            }
            RequestFromHolder.set(requestFrom);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            RequestFromHolder.remove();
        }
    }

    private String getRequestIp(HttpServletRequest request) {
        if (request != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Looking at request to determine IP address");
            }
            // get forwarded for first
            List<String> forwardedForHeaders = Collections.list(request.getHeaders(X_FORWARDED_FOR));
            if (logger.isDebugEnabled()) {
                logger.debug("Forwarded for headers " + forwardedForHeaders);
            }
            for (String forwardedForHeader : forwardedForHeaders) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Looking at X-Forwarded-For header value " + forwardedForHeader);
                }
                String[] forwardedForAddressArr = forwardedForHeader.split(",");
                for (String forwardedForAddress : forwardedForAddressArr) {
                    forwardedForAddress = StringUtils.trim(forwardedForAddress);
                    return forwardedForAddress;
                }
            }
            // neither user ip nor forwarded for set or valid, use request remote addr
            String remoteAddr = request.getRemoteAddr();
            if (logger.isDebugEnabled()) {
                logger.debug("No valid X-Forwarded-For headers returning request remote address " + remoteAddr);
            }
            return remoteAddr;
        }
        return null;
    }
}
