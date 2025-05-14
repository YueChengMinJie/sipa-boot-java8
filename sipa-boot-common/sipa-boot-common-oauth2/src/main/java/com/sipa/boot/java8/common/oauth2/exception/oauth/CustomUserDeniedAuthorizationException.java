package com.sipa.boot.java8.common.oauth2.exception.oauth;

import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sipa.boot.java8.common.oauth2.exception.serializer.CustomOAuth2ExceptionSerializer;

@JsonSerialize(using = CustomOAuth2ExceptionSerializer.class)
public class CustomUserDeniedAuthorizationException extends UserDeniedAuthorizationException {
    public CustomUserDeniedAuthorizationException(String msg) {
        super(msg);
    }

    /***
     * Same with InvalidTokenException. 没有权限，不等于invalid token.
     *
     * @return oauth2 error code.
     */
    @Override
    public String getOAuth2ErrorCode() {
        return "unauthorized";
    }
}
