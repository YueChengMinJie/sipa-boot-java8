package com.sipa.boot.java8.common.oauth2.exception.oauth;

import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sipa.boot.java8.common.oauth2.exception.serializer.CustomOAuth2ExceptionSerializer;

@JsonSerialize(using = CustomOAuth2ExceptionSerializer.class)
public class CustomUnsupportedGrantTypeException extends UnsupportedGrantTypeException {
    public CustomUnsupportedGrantTypeException(String msg) {
        super(msg);
    }
}
