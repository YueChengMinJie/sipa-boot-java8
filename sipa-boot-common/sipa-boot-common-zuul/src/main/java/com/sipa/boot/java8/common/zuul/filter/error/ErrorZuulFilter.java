package com.sipa.boot.java8.common.zuul.filter.error;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * @author zhouxiajie
 * @date 2019-02-03
 */
@Component
public class ErrorZuulFilter extends ZuulFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorZuulFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.ERROR_TYPE;
    }

    /**
     * 1
     */
    @Override
    public int filterOrder() {
        return FilterConstants.SEND_ERROR_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().getThrowable() != null;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();

        Throwable throwable = ctx.getThrowable();

        LOGGER.info("{} error class: {} {}", StringUtils.repeat("*", 10), throwable.getClass(),
            StringUtils.repeat("*", 10));

        return null;
    }
}
