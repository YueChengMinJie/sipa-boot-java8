package com.sipa.boot.java8.common.zuul.filter.pre;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.sipa.boot.java8.common.archs.canary.context.RibbonFilterContextHolder;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.CookieUtils;

/**
 * @author zhouxiajie
 * @date 2021/5/19
 */
@Component
@ConditionalOnProperty(prefix = "sipa.boot.zuul", name = "enableCanary", havingValue = "true")
public class CanaryPreZuulFilter extends ZuulFilter {
    private static final Log LOGGER = LogFactory.get(CanaryPreZuulFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RibbonFilterContextHolder.clearCurrentContext();

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        List<String> canaries =
            CookieUtils.getCookieValuesByName(request.getCookies(), SipaBootCommonConstants.Canary.COOKIE);

        if (CollectionUtils.isNotEmpty(canaries)) {
            String canary = canaries.get(0);
            LOGGER.info("Canary route [{}], canary [{}]", request.getRequestURI(), canary);
            RibbonFilterContextHolder.getCurrentContext().add(SipaBootCommonConstants.Canary.METADATA, canary);
        }

        return null;
    }
}
