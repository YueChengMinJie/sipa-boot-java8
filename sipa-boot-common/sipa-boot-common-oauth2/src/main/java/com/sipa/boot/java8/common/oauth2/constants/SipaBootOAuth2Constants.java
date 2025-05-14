package com.sipa.boot.java8.common.oauth2.constants;

/**
 * @author zhouxiajie
 * @date 2019-02-03
 */
public class SipaBootOAuth2Constants {
    public static final String LOGIN_TYPE_PARAM = "type";

    public static final String LOGIN_SCOPE_PARAM = "scope";

    public static final String LOGIN_TENANT_ID = "tenantId";

    public static final String LOGIN_PASSWORD = "password";

    public interface CustomOAuth2ErrorCode {
        String LOGIN_TIME_TOO_MANY = "custom_oauth2_login_time_too_many";

        String USER_EXPIRE_TIME_ARRIVAL = "custom_oauth2_expire_time_arrival";
    }

    public interface CustomOAuth2ErrorMessage {
        String OAUTH2_ERROR = "OAuth2 error";

        String LOGIN_TIME_TOO_MANY = "Custom OAuth2 login time too many";

        String USER_EXPIRE_TIME_ARRIVAL = "Custom OAuth2 expire time arrival";
    }
}
