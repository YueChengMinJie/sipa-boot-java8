package com.sipa.boot.java8.common.zuul.filter.pre;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.netflix.zuul.context.RequestContext;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.zuul.common.ZuulConstants;
import com.sipa.boot.java8.common.zuul.filter.abst.AbstractPreZuulFilter;
import com.sipa.boot.java8.common.zuul.util.HttpServletUtils;

/**
 * @author zhouxiajie
 * @date 2019-01-31
 */
@Component
public class BasicAuthPreZuulFilter extends AbstractPreZuulFilter {
    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = HttpServletUtils.getRequest();

        RequestContext ctx = RequestContext.getCurrentContext();

        return ZuulConstants.OAUTH2_TOKEN_ENDPOINT.equals(request.getRequestURI()) && ctx.sendZuulResponse();
    }

    @Override
    public Object run() {
        final RequestContext ctx = RequestContext.getCurrentContext();

        String encoded = Base64Utils
            .encodeToString((clientId + SipaBootCommonConstants.COLON + clientSecret).getBytes(StandardCharsets.UTF_8));

        ctx.addZuulRequestHeader(ZuulConstants.AUTHORIZATION_HEADER,
            ZuulConstants.BASIC_AUTH_HEADER_PREFIX + SipaBootCommonConstants.EMPTY + encoded);

        customHeader(ctx);

        return null;
    }
}
