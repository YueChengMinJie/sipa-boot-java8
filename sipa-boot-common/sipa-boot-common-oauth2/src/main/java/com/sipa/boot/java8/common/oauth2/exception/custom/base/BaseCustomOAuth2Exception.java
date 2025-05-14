package com.sipa.boot.java8.common.oauth2.exception.custom.base;

import java.util.Optional;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import com.sipa.boot.java8.common.oauth2.constants.SipaBootOAuth2Constants;

/**
 * @author zhouxiajie
 * @date 2020/10/21
 */
public abstract class BaseCustomOAuth2Exception extends OAuth2Exception {
    private final String code;

    public BaseCustomOAuth2Exception(String code, String msg) {
        super(Optional.ofNullable(msg).orElse(SipaBootOAuth2Constants.CustomOAuth2ErrorMessage.OAUTH2_ERROR));
        this.code = code;
    }

    @Override
    public String getOAuth2ErrorCode() {
        return this.code;
    }
}
