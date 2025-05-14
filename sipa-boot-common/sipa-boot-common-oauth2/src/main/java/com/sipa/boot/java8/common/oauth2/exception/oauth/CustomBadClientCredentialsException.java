package com.sipa.boot.java8.common.oauth2.exception.oauth;

import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sipa.boot.java8.common.oauth2.exception.serializer.CustomOAuth2ExceptionSerializer;

/**
 * @author zhouxiajie
 * @since 2020/2/15 16:26
 */
@JsonSerialize(using = CustomOAuth2ExceptionSerializer.class)
public class CustomBadClientCredentialsException extends BadClientCredentialsException {
    /**
     * Same with InvalidClientException.
     * <p>
     * this exception show basic auth error, because of client secret, not client id.
     *
     * @return oauth2 error code.
     */
    @Override
    public String getOAuth2ErrorCode() {
        return "invalid_client_bad_credentials";
    }
}
