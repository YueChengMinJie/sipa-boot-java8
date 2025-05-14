package com.sipa.boot.java8.common.oauth2.enumerate;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipa.boot.java8.common.oauth2.constants.SipaBootOAuth2Constants;

public enum EOAuth2Error {
    // @formatter:off
    ERROR                                   (400, 20100, "error", "OAuth2 error.", false),
    ACCESS_DENIED                           (403, 20101, "access_denied", "Access denied.", false),
    INVALID_CLIENT                          (401, 20102, "invalid_client", "Invalid client.", false),
    REDIRECT_URI_MISMATCH                   (400, 20103, "redirect_uri_mismatch", "Redirect uri mismatch your client.", false),
    UNSUPPORTED_RESPONSE_TYPE               (400, 20104, "unsupported_response_type", "Unsupported response type.", false),
    INVALID_GRANT                           (400, 20105, "invalid_grant", "Invalid grant.", false),
    UNSUPPORTED_GRANT_TYPE                  (400, 20106, "unsupported_grant_type", "Unsupported grant type.", false),
    UNAUTHORIZED_CLIENT                     (401, 20107, "unauthorized_client", "Unauthorized client.", false),
    INVALID_REQUEST                         (400, 20108, "invalid_request", "Invalid request.", false),
    INVALID_TOKEN                           (401, 20109, "invalid_token", "Invalid token.", false),
    INVALID_SCOPE                           (400, 20110, "invalid_scope", "Invalid scope.", false),

    AUTHORIZATION_CODE_EXPIRED              (401, 20111, "authorization_code_expired", "Authorization code was expired.", false),
    AUTHORIZATION_CODE_NOT_FOR_YOUR_CLIENT  (401, 20112, "authorization_code_not_for_your_client", "Authorization code is not for your client.", false),
    AUTHORIZATION_CODE_NOT_FOUND            (401, 20113, "authorization_code_not_found", "Authorization code was not found.", false),

    ACCESS_TOKEN_NOT_FOUND                  (401, 20114, "access_token_not_found", "Access token was not found.", false),

    REFRESH_TOKEN_NOT_FOUND                 (401, 20115, "refresh_token_not_found", "Refresh token was not found.", false),
    REFRESH_TOKEN_EXPIRED                   (401, 20116, "refresh_token_expired", "Refresh token was expired.", false),

    INVALID_CODE_TYPE                       (401, 20117, "invalid_code_type", "Invalid code type.", false),
    AUTHORIZATION_CODE_MISMATCH_STATE       (400, 20118, "authorization_code_mismatch_state", "Authorization code mismatched previous state.", false),

    // 没有认证
    UNAUTHENTICATED                         (401, 20119, "unauthenticated", "Unauthenticated request.", false),

    // 没有权限
    UNAUTHORIZED                            (401, 20120, "unauthorized", "Unauthorized request.", false),

    METHOD_NOT_ALLOWED                      (405, 20121, "method_not_allowed", "Method not allowed.", false),

    BASIC_AUTH_FAIL                         (401, 20122, "invalid_client_bad_credentials", "Bad client credentials.", false),
    REDIRECT_MISMATCH                       (400, 20123, "invalid_grant_redirect_mismatch", "Redirect mismatch.", false),
    INSUFFICIENT_SCOPE                      (403, 20124, "insufficient_scope", "Insufficient scope.", false),
    UNAUTHORIZED_USER                       (401, 20125, "unauthorized_user", UNAUTHORIZED.getErrorMessage(), false),
    
    // 自定义
    CUSTOM_OAUTH2_LOGIN_TIME_TOO_MANY       (400, 20126, SipaBootOAuth2Constants.CustomOAuth2ErrorCode.LOGIN_TIME_TOO_MANY, SipaBootOAuth2Constants.CustomOAuth2ErrorMessage.LOGIN_TIME_TOO_MANY, true),
    CUSTOM_OAUTH2_EXPIRE_TIME_ARRIVAL       (400, 20127, SipaBootOAuth2Constants.CustomOAuth2ErrorCode.USER_EXPIRE_TIME_ARRIVAL, SipaBootOAuth2Constants.CustomOAuth2ErrorMessage.USER_EXPIRE_TIME_ARRIVAL, true);
    // @formatter:on

    private final int httpStatusCode;

    private final int numericCode;

    private final String errorCode;

    private final String errorMessage;

    private final boolean useOriginalMessage;

    private static final Logger LOGGER = LoggerFactory.getLogger(EOAuth2Error.class);

    private static final Map<String, EOAuth2Error> ERROR_CODES = new HashMap<>();

    static {
        for (EOAuth2Error error : EOAuth2Error.values()) {
            ERROR_CODES.put(error.getErrorCode(), error);
        }
    }

    EOAuth2Error(int httpStatusCode, int numericCode, String errorCode, String errorMessage,
        boolean useOriginalMessage) {
        this.httpStatusCode = httpStatusCode;
        this.numericCode = numericCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.useOriginalMessage = useOriginalMessage;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getNumericCode() {
        return numericCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isUseOriginalMessage() {
        return useOriginalMessage;
    }

    public static EOAuth2Error create(String oauthErrorCode) {
        if (ERROR_CODES.containsKey(oauthErrorCode)) {
            return ERROR_CODES.get(oauthErrorCode);
        } else {
            LOGGER.info("Unknown oauth error code [{}], return default.", oauthErrorCode);
            return EOAuth2Error.ERROR;
        }
    }
}
