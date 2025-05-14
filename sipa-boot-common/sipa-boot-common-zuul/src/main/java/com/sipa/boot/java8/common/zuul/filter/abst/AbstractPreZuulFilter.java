package com.sipa.boot.java8.common.zuul.filter.abst;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.sipa.boot.java8.common.archs.request.DeviceIdHolder;
import com.sipa.boot.java8.common.archs.request.RequestFromHolder;
import com.sipa.boot.java8.common.archs.request.RequestIdHolder;
import com.sipa.boot.java8.common.archs.request.UserAgentHolder;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.utils.UuidUtils;

/**
 * @author zhouxiajie
 * @date 2019-05-26
 */
public abstract class AbstractPreZuulFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * -4
     */
    @Override
    public int filterOrder() {
        return FilterConstants.SERVLET_DETECTION_FILTER_ORDER - 1;
    }

    protected void customHeader(RequestContext ctx) {
        if (StringUtils.isBlank(ctx.getRequest().getHeader(RequestIdHolder.X_REQUEST_ID))) {
            ctx.addZuulRequestHeader(RequestIdHolder.X_REQUEST_ID, UuidUtils.generator());
        } else {
            ctx.addZuulRequestHeader(RequestIdHolder.X_REQUEST_ID,
                ctx.getRequest().getHeader(RequestIdHolder.X_REQUEST_ID));
        }

        ctx.addZuulRequestHeader(DeviceIdHolder.X_DEVICE_ID, ctx.getRequest().getHeader(DeviceIdHolder.X_DEVICE_ID));

        if (StringUtils.isBlank(ctx.getRequest().getHeader(RequestIdHolder.X_REQUEST_ID))) {
            ctx.addZuulRequestHeader(RequestFromHolder.X_REQUEST_FROM, SipaBootCommonConstants.GATEWAY);
        } else {
            ctx.addZuulRequestHeader(RequestFromHolder.X_REQUEST_FROM,
                ctx.getRequest().getHeader(RequestFromHolder.X_REQUEST_FROM));
        }

        ctx.addZuulRequestHeader(UserAgentHolder.X_USER_AGENT, ctx.getRequest().getHeader(UserAgentHolder.USER_AGENT));
    }
}
