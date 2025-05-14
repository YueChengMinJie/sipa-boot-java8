package com.sipa.boot.java8.common.oauth2.exception.oauth;

import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sipa.boot.java8.common.oauth2.exception.serializer.CustomOAuth2ExceptionSerializer;

@JsonSerialize(using = CustomOAuth2ExceptionSerializer.class)
public class CustomRedirectMismatchException extends RedirectMismatchException {
    public CustomRedirectMismatchException(String msg) {
        super(msg);
    }

    /**
     * Same with InvalidGrantException.
     * <p>
     * 授权码类型时，授权服务器无法找到重定向的URL.
     *
     * @return oauth2 error code.
     */
    @Override
    public String getOAuth2ErrorCode() {
        return "invalid_grant_redirect_mismatch";
    }
}
