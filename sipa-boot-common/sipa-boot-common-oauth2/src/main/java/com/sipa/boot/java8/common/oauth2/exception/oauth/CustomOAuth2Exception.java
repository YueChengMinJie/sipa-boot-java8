package com.sipa.boot.java8.common.oauth2.exception.oauth;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sipa.boot.java8.common.oauth2.exception.serializer.CustomOAuth2ExceptionSerializer;

@JsonSerialize(using = CustomOAuth2ExceptionSerializer.class)
public class CustomOAuth2Exception extends OAuth2Exception {
    public CustomOAuth2Exception(String msg) {
        super(msg);
    }

    /**
     * Same with CustomInvalidRequestException.
     * <p>
     * this is common auth2 error.
     *
     * @return oauth2 error code.
     */
    @Override
    public String getOAuth2ErrorCode() {
        return "error";
    }
}
