package com.sipa.boot.java8.common.zuul.common;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author zhouxiajie
 * @date 2019-01-18
 */
public class ZuulConstants {
    public static final String TOKEN_BASE_URI = ZuulConstants.TOKEN_BASE1
        + StringUtils.repeat(SipaBootCommonConstants.ASTERISK, SipaBootCommonConstants.Number.INT_2);

    public static final String TOKEN_BASE1 = "/api/";

    public static final String TOKEN_BASE2 = "/graphql";

    public static final String TOKEN_BASE3 = "/rbac";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String PUBLIC_KEY = "public.key";

    public static final String SECURITY_PASS_ROLE = "ROLE_ANONYMOUS";

    public static final String TOKEN_HEADER = "Sipa Boot-Access-Token";

    public static final String BASIC_AUTH_HEADER_PREFIX = "Basic";

    public static final String OAUTH2_TOKEN_ENDPOINT = "/uaa/oauth/token";

    public static final String IDEMPOTENT = "idempotent";

    public static final List<String> CONTENT_TYPE_WHITE_LIST = Lists.newArrayList("form-data", "x-msdownload");
}
