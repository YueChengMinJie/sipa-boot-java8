package com.sipa.boot.java8.common.zuul.security.oauth2.provider.authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.zuul.common.ZuulConstants;

/**
 * @author caszhou
 * @date 2021/8/4
 */
@Component
public class HeaderTokenExtractor extends BearerTokenExtractor {
    @Override
    protected String extractToken(HttpServletRequest request) {
        String token = super.extractToken(request);
        if (StringUtils.isBlank(token) && !ZuulConstants.OAUTH2_TOKEN_ENDPOINT.equals(request.getRequestURI())) {
            token = getTokenFromCookie(request.getCookies());
        }
        return token;
    }

    private String getTokenFromCookie(Cookie[] cookies) {
        String token = null;
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ZuulConstants.TOKEN_HEADER)) {
                    token = StringUtils.trimToNull(cookie.getValue());
                    break;
                }
            }
        }
        return token;
    }
}
