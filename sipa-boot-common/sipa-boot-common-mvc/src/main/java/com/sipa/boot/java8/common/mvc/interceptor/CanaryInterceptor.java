package com.sipa.boot.java8.common.mvc.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sipa.boot.java8.common.archs.canary.context.RibbonFilterContextHolder;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.CookieUtils;

/**
 * @author zhouxiajie
 * @date 2021/5/19
 */
public class CanaryInterceptor extends HandlerInterceptorAdapter {
    private static final Log LOGGER = LogFactory.get(CanaryInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        RibbonFilterContextHolder.clearCurrentContext();
        List<String> canaries =
            CookieUtils.getCookieValuesByName(request.getCookies(), SipaBootCommonConstants.Canary.COOKIE);

        if (CollectionUtils.isNotEmpty(canaries)) {
            LOGGER.info("Canary route [{}], canary [{}]", request.getRequestURI(), canaries.get(0));
            RibbonFilterContextHolder.getCurrentContext().add(SipaBootCommonConstants.Canary.METADATA, canaries.get(0));
        }

        return true;
    }
}
