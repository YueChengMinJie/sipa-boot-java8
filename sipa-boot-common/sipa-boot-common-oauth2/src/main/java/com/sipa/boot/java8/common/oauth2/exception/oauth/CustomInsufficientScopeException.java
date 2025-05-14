package com.sipa.boot.java8.common.oauth2.exception.oauth;

import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sipa.boot.java8.common.oauth2.exception.serializer.CustomOAuth2ExceptionSerializer;

/**
 * @author zhouxiajie
 * @since 2020/2/15 16:37
 */
@JsonSerialize(using = CustomOAuth2ExceptionSerializer.class)
public class CustomInsufficientScopeException extends InsufficientScopeException {
    public CustomInsufficientScopeException(String msg) {
        super(msg);
    }
}
