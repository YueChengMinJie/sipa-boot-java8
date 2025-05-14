package com.sipa.boot.java8.common.mvc.filter.base;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sipa.boot.java8.common.archs.request.DeviceIdHolder;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public class DeviceIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String deviceId = request.getHeader(DeviceIdHolder.X_DEVICE_ID);
        if (StringUtils.isNotBlank(deviceId)) {
            DeviceIdHolder.set(deviceId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            DeviceIdHolder.remove();
        }
    }
}
