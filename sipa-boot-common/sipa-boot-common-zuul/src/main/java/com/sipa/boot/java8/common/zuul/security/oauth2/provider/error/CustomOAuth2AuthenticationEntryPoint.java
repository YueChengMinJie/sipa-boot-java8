package com.sipa.boot.java8.common.zuul.security.oauth2.provider.error;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.zuul.common.ZuulConstants;

/**
 * @author caszhou
 * @date 2021/8/4
 */
@Component
public class CustomOAuth2AuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {
    @PostConstruct
    public void init() {
        this.setExceptionTranslator(new ResourceServerWebResponseExceptionTranslator());
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        if (request.getRequestURI().startsWith(ZuulConstants.TOKEN_BASE3)) {
            response.sendRedirect("/");
            response.setHeader("Cache-Control", "no-store");
        } else {
            super.commence(request, response, authException);
        }
    }
}
