package com.sipa.boot.java8.common.oauth2.exception.oauth;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sipa.boot.java8.common.oauth2.exception.serializer.CustomOAuth2ExceptionSerializer;

/**
 * @author zhouxiajie
 * @date 2020/10/21
 */
@JsonSerialize(using = CustomOAuth2ExceptionSerializer.class)
public class CustomInternalAuthenticationServiceException extends OAuth2Exception {
    private final String code;

    public CustomInternalAuthenticationServiceException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    @Override
    public String getOAuth2ErrorCode() {
        return code;
    }
}
