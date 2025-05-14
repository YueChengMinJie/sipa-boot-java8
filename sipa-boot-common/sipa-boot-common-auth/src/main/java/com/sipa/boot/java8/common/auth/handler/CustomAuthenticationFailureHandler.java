package com.sipa.boot.java8.common.auth.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.java8.common.auth.exception.InvalidJwtException;
import com.sipa.boot.java8.common.auth.exception.MethodNotSupportedException;
import com.sipa.boot.java8.common.auth.exception.SipaBootBadCredentialsException;
import com.sipa.boot.java8.common.auth.exception.SipaBootExpiredJwtException;
import com.sipa.boot.java8.common.dtos.ResponseWrapper;
import com.sipa.boot.java8.common.enums.EResCode;
import com.sipa.boot.java8.common.services.IMessageService;

/**
 * @author zhouxiajie
 * @date 2018/2/6
 */
@Component("failureHandler")
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper mapper;

    private final IMessageService sipaBootMessageService;

    @Autowired
    public CustomAuthenticationFailureHandler(ObjectMapper mapper,
        @Qualifier("sipaBootMessageService") IMessageService sipaBootMessageService) {
        this.mapper = mapper;
        this.sipaBootMessageService = sipaBootMessageService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException e) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (e instanceof SipaBootBadCredentialsException) {
            mapper.writeValue(response.getWriter(), ResponseWrapper.errorOf(e.getMessage(), EResCode.AUTH, 401));
        } else if (e instanceof BadCredentialsException) {
            mapper.writeValue(response.getWriter(),
                ResponseWrapper.errorOf(sipaBootMessageService.getMessage("authentication.fail"), EResCode.AUTH, 401));
        } else if (e instanceof MethodNotSupportedException) {
            mapper.writeValue(response.getWriter(), ResponseWrapper
                .errorOf(sipaBootMessageService.getMessage("authentication.refuse"), EResCode.AUTH, 401));
        } else if (e instanceof InvalidJwtException) {
            mapper.writeValue(response.getWriter(), ResponseWrapper
                .errorOf(sipaBootMessageService.getMessage("authentication.refuse"), EResCode.AUTH, 401));
        } else if (e instanceof SipaBootExpiredJwtException) {
            mapper.writeValue(response.getWriter(), ResponseWrapper
                .errorOf(sipaBootMessageService.getMessage("authentication.expire"), EResCode.AUTH, 401));
        } else if (e instanceof InsufficientAuthenticationException) {
            mapper.writeValue(response.getWriter(), ResponseWrapper
                .errorOf(sipaBootMessageService.getMessage("authentication.no_access"), EResCode.AUTH, 401));
        } else if (e instanceof AuthenticationServiceException) {
            mapper.writeValue(response.getWriter(), ResponseWrapper
                .errorOf(sipaBootMessageService.getMessage("authentication.refuse"), EResCode.AUTH, 401));
        } else {
            mapper.writeValue(response.getWriter(),
                ResponseWrapper.errorOf(sipaBootMessageService.getMessage("authentication"), EResCode.AUTH, 401));
        }
    }
}
