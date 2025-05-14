package com.sipa.boot.java8.common.oauth2.exception.custom;

import com.sipa.boot.java8.common.oauth2.exception.custom.base.BaseCustomOAuth2Exception;

/**
 * @author zhouxiajie
 * @date 2020/10/21
 */
public class CustomOAuth2Exception extends BaseCustomOAuth2Exception {
    public CustomOAuth2Exception(String code, String msg) {
        super(code, msg);
    }
}
