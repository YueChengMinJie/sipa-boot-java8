package com.sipa.boot.java8.common.zuul.security.oauth2.common.exceptions;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sipa.boot.java8.common.oauth2.exception.serializer.CustomOAuth2ExceptionSerializer;

/**
 * @author songjianming
 * @date 2021/11/11
 */
@JsonSerialize(using = CustomOAuth2ExceptionSerializer.class)
public class ReOAuth2Exception extends OAuth2Exception {
    public ReOAuth2Exception(String msg, Throwable t) {
        super(msg, t);
    }
}
