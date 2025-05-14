package com.sipa.boot.java8.common.ws.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.ws.token.WsAnonyAuthenticationToken;

/**
 * @author zhouxiajie
 * @date 2019-02-03
 */
public class WsPermitAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public WsPermitAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        WsAnonyAuthenticationToken token = new WsAnonyAuthenticationToken(
            Arrays.stream(StringUtils.split(
                StringUtils.trimToEmpty(request.getHeader(SipaBootCommonConstants.SIPA_BOOT_AUTHORITIES_HEADER)),
                SipaBootCommonConstants.COMMA)).map(SimpleGrantedAuthority::new).collect(Collectors.toList()),
            StringUtils.trimToEmpty(request.getHeader(SipaBootCommonConstants.SIPA_BOOT_USER_ID_HEADER)),
            StringUtils.trimToEmpty(request.getHeader(SipaBootCommonConstants.SIPA_BOOT_TENANT_ID_HEADER)));

        token.setAuthenticated(true);
        return token;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }
}
