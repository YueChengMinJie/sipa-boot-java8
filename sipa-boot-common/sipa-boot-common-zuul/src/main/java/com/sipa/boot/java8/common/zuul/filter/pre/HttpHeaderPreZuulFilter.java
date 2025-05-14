package com.sipa.boot.java8.common.zuul.filter.pre;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import com.netflix.zuul.context.RequestContext;
import com.sipa.boot.java8.common.archs.request.ClientIdHolder;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.oauth2.entity.SipaBootUser;
import com.sipa.boot.java8.common.oauth2.enumerate.ELoginStrategy;
import com.sipa.boot.java8.common.zuul.common.ZuulConstants;
import com.sipa.boot.java8.common.zuul.filter.abst.AbstractPreZuulFilter;
import com.sipa.boot.java8.common.zuul.property.ZuulProperties;
import com.sipa.boot.java8.common.zuul.util.HttpServletUtils;

/**
 * @author zhouxiajie
 * @date 2019-02-03
 */
@Component
public class HttpHeaderPreZuulFilter extends AbstractPreZuulFilter {
    private final ZuulProperties zuulProperties;

    public HttpHeaderPreZuulFilter(ZuulProperties zuulProperties) {
        this.zuulProperties = zuulProperties;
    }

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = HttpServletUtils.getRequest();

        RequestContext ctx = RequestContext.getCurrentContext();

        return tokenBase(request) && ctx.sendZuulResponse();
    }

    private boolean tokenBase(HttpServletRequest request) {
        return request.getRequestURI().startsWith(ZuulConstants.TOKEN_BASE1)
            || ZuulConstants.TOKEN_BASE2.equals(request.getRequestURI())
            || ZuulConstants.TOKEN_BASE3.equals(request.getRequestURI());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object run() {
        final RequestContext ctx = RequestContext.getCurrentContext();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication)authentication;

            if (ELoginStrategy.STATEFUL == zuulProperties.getSecurity().getLoginStrategy()) {
                SipaBootUser sipaBootUser = (SipaBootUser)oAuth2Authentication.getPrincipal();

                ctx.addZuulRequestHeader(SipaBootCommonConstants.SIPA_BOOT_USER_ID_HEADER,
                    Objects.toString(sipaBootUser.getId()));

                ctx.addZuulRequestHeader(SipaBootCommonConstants.SIPA_BOOT_TENANT_ID_HEADER,
                    Objects.toString(sipaBootUser.getTenantId()));
            } else {
                OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)oAuth2Authentication.getDetails();

                Map<String, Object> claims = (Map<String, Object>)details.getDecodedDetails();

                ctx.addZuulRequestHeader(SipaBootCommonConstants.SIPA_BOOT_USER_ID_HEADER,
                    Objects.toString(claims.get(SipaBootCommonConstants.SIPA_BOOT_USER_ID_KEY), null));

                ctx.addZuulRequestHeader(SipaBootCommonConstants.SIPA_BOOT_TENANT_ID_HEADER,
                    Objects.toString(claims.get(SipaBootCommonConstants.SIPA_BOOT_TENANT_ID_KEY), null));
            }

            OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();

            ctx.addZuulRequestHeader(SipaBootCommonConstants.SIPA_BOOT_SCOPE_HEADER,
                StringUtils.join(oAuth2Request.getScope().toArray()));

            ctx.addZuulRequestHeader(ClientIdHolder.X_CLIENT_ID, oAuth2Request.getClientId());

            ctx.addZuulRequestHeader(SipaBootCommonConstants.SIPA_BOOT_AUTHORITIES_HEADER,
                StringUtils.join(oAuth2Authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()), SipaBootCommonConstants.COMMA));
        }

        customHeader(ctx);

        return null;
    }
}
