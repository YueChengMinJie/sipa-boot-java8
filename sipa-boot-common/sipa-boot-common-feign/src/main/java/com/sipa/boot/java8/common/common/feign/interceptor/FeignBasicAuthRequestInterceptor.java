package com.sipa.boot.java8.common.common.feign.interceptor;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.utils.AuthUtils;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author zhouxiajie
 * @date 2019-04-10
 */
public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header(SipaBootCommonConstants.SIPA_BOOT_USER_ID_HEADER, AuthUtils.getUserId());
        template.header(SipaBootCommonConstants.SIPA_BOOT_TENANT_ID_HEADER, AuthUtils.getTenantId());
    }
}
