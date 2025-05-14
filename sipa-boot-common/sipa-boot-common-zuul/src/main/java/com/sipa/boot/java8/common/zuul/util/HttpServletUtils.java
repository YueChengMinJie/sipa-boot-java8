package com.sipa.boot.java8.common.zuul.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netflix.zuul.context.RequestContext;

/**
 * @author zhouxiajie
 * @date 2019-01-18
 */
public class HttpServletUtils {
    public static HttpServletRequest getRequest() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getRequest();
    }

    public static HttpServletResponse getResponse() {
        RequestContext context = RequestContext.getCurrentContext();
        return context.getResponse();
    }
}
