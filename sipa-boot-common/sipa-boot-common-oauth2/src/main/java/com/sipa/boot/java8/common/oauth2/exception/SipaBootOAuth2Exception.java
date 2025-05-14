package com.sipa.boot.java8.common.oauth2.exception;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.sipa.boot.java8.common.exceptions.ApplicationException;
import com.sipa.boot.java8.common.oauth2.enumerate.EOAuth2Error;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class SipaBootOAuth2Exception extends ApplicationException {
    public static final String DEFAULT_OAUTH2_EXCEPTION_ERROR_CODE_PREFIX = "errors.com.sipa.boot.oauth2.";

    public static final String DEFAULT_OAUTH2_EXCEPTION_ERROR_CODE = "error";

    public static final String DEFAULT_OAUTH2_EXCEPTION_ERROR_MESSAGE = "OAuth error.";

    private String error = DEFAULT_OAUTH2_EXCEPTION_ERROR_CODE;

    private String description = DEFAULT_OAUTH2_EXCEPTION_ERROR_MESSAGE;

    private String uri;

    private String state;

    private String scope;

    private String redirectUri;

    public SipaBootOAuth2Exception() {
        this(EOAuth2Error.ERROR);
    }

    public SipaBootOAuth2Exception(EOAuth2Error error) {
        this(error.getHttpStatusCode(), error.getNumericCode(),
            DEFAULT_OAUTH2_EXCEPTION_ERROR_CODE_PREFIX + error.getErrorCode(), error.getErrorMessage());
        // set oauth error
        setError(error.getErrorCode());
        setDescription(error.getErrorMessage());
    }

    public SipaBootOAuth2Exception(int httpStatusCode, int numericErrorCode, String errorCode, String pattern,
        Object... args) {
        super(httpStatusCode, numericErrorCode, errorCode, pattern, args);
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    /**
     * The OAuth2 error code. For this base exception is always invalid_request
     *
     * @return The OAuth2 error code.
     */
    public String getError() {
        return error;
    }

    /**
     * @return The OAuth2 error description.
     */
    public String getDescription() {
        return description;
    }

    public String getUri() {
        return uri;
    }

    public String getState() {
        return state;
    }

    public String getScope() {
        return scope;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public static OAuth2ExceptionBuilder builder() {
        return new OAuth2ExceptionBuilder();
    }

    public static final class OAuth2ExceptionBuilder {
        private String error = DEFAULT_OAUTH2_EXCEPTION_ERROR_CODE;

        private String description = DEFAULT_OAUTH2_EXCEPTION_ERROR_MESSAGE;

        private String uri;

        private String state;

        private String scope;

        private String redirectUri;

        private OAuth2ExceptionBuilder() {}

        public OAuth2ExceptionBuilder error(String error) {
            this.error = error;
            return this;
        }

        public OAuth2ExceptionBuilder description(String description) {
            this.description = description;
            return this;
        }

        public OAuth2ExceptionBuilder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public OAuth2ExceptionBuilder state(String state) {
            this.state = state;
            return this;
        }

        public OAuth2ExceptionBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public OAuth2ExceptionBuilder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public SipaBootOAuth2Exception build() {
            SipaBootOAuth2Exception sipaBootOAuth2Exception = new SipaBootOAuth2Exception();
            sipaBootOAuth2Exception.setError(error);
            sipaBootOAuth2Exception.setDescription(description);
            sipaBootOAuth2Exception.setUri(uri);
            sipaBootOAuth2Exception.setState(state);
            sipaBootOAuth2Exception.setScope(scope);
            sipaBootOAuth2Exception.setRedirectUri(redirectUri);
            return sipaBootOAuth2Exception;
        }
    }
}
