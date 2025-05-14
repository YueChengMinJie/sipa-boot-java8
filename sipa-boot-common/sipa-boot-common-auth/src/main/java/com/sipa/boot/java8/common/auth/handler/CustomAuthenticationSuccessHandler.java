package com.sipa.boot.java8.common.auth.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.java8.common.auth.factory.JwtFactory;
import com.sipa.boot.java8.common.auth.model.UserContext;
import com.sipa.boot.java8.common.auth.model.token.JwtToken;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.dtos.ResponseWrapper;

/**
 * @author zhouxiajie
 * @date 2018/2/6
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper mapper;

    private final JwtFactory tokenFactory;

    @Autowired
    public CustomAuthenticationSuccessHandler(final ObjectMapper mapper, final JwtFactory tokenFactory) {
        this.mapper = mapper;
        this.tokenFactory = tokenFactory;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        UserContext userContext = (UserContext)authentication.getPrincipal();

        JwtToken accessToken = tokenFactory.createAccessJwtToken(userContext);
        JwtToken refreshToken = tokenFactory.createRefreshToken(userContext);

        Map<String, String> tokenMap = new HashMap<>(16);
        tokenMap.put("token", accessToken.getToken());
        tokenMap.put("refreshToken", refreshToken.getToken());
        tokenMap.put("authorities", getAuthorities(userContext));

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        mapper.writeValue(response.getWriter(), ResponseWrapper.successOf(tokenMap));

        clearAuthenticationAttributes(request);
    }

    private String getAuthorities(UserContext userContext) {
        return StringUtils.join(
            userContext.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()),
            SipaBootCommonConstants.COMMA);
    }

    /**
     * Removes temporary authentication-related data which may have been stored in the session during the authentication
     * process..
     */
    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
