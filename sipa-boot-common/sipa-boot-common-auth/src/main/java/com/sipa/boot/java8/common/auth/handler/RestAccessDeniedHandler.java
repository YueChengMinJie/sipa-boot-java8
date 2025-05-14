package com.sipa.boot.java8.common.auth.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.java8.common.dtos.ResponseWrapper;
import com.sipa.boot.java8.common.enums.EResCode;
import com.sipa.boot.java8.common.services.IMessageService;

/**
 * @author zhouxiajie
 * @date 2018/2/5
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper mapper;

    private final IMessageService sipaBootMessageService;

    @Autowired
    public RestAccessDeniedHandler(ObjectMapper mapper,
        @Qualifier("sipaBootMessageService") IMessageService sipaBootMessageService) {
        this.mapper = mapper;
        this.sipaBootMessageService = sipaBootMessageService;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        mapper.writeValue(response.getWriter(),
            ResponseWrapper.errorOf(sipaBootMessageService.getMessage("authentication.no_access"), EResCode.AUTH, 401));
    }
}
