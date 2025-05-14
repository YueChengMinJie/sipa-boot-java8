package com.sipa.boot.java8.common.zuul.security.oauth2.provider.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.sipa.boot.java8.common.zuul.security.oauth2.common.exceptions.ReOAuth2Exception;

/**
 * @author songjianming
 * @date 2021/11/11
 */
public class ResourceServerWebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {
    private final ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

    public ResponseEntity<OAuth2Exception> translate(Exception e) {
        Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(e);
        Exception ase =
            (OAuth2Exception)this.throwableAnalyzer.getFirstThrowableOfType(ReOAuth2Exception.class, causeChain);
        if (ase != null) {
            return this.handleOAuth2Exception((OAuth2Exception)ase);
        } else {
            ase = (AuthenticationException)this.throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class,
                causeChain);
            if (ase != null) {
                return this.handleOAuth2Exception(new UnauthorizedException(e.getMessage(), e));
            } else {
                ase = (AccessDeniedException)this.throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class,
                    causeChain);
                if (ase != null) {
                    return this.handleOAuth2Exception(new ForbiddenException(ase.getMessage(), ase));
                } else {
                    ase = (HttpRequestMethodNotSupportedException)this.throwableAnalyzer
                        .getFirstThrowableOfType(HttpRequestMethodNotSupportedException.class, causeChain);
                    return ase != null ? this.handleOAuth2Exception(new MethodNotAllowed(ase.getMessage(), ase))
                        : this.handleOAuth2Exception(
                            new ServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e));
                }
            }
        }
    }

    private ResponseEntity<OAuth2Exception> handleOAuth2Exception(OAuth2Exception e) {
        int status = e.getHttpErrorCode();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        if (status == HttpStatus.UNAUTHORIZED.value() || e instanceof InsufficientScopeException) {
            headers.set("WWW-Authenticate", String.format("%s %s", "Bearer", e.getSummary()));
        }

        return new ResponseEntity<>(e, headers, HttpStatus.valueOf(status));
    }

    private static class MethodNotAllowed extends ReOAuth2Exception {
        public MethodNotAllowed(String msg, Throwable t) {
            super(msg, t);
        }

        public String getOAuth2ErrorCode() {
            return "method_not_allowed";
        }

        public int getHttpErrorCode() {
            return 405;
        }
    }

    private static class UnauthorizedException extends ReOAuth2Exception {
        public UnauthorizedException(String msg, Throwable t) {
            super(msg, t);
        }

        public String getOAuth2ErrorCode() {
            return "unauthorized";
        }

        public int getHttpErrorCode() {
            return 401;
        }
    }

    private static class ServerErrorException extends ReOAuth2Exception {
        public ServerErrorException(String msg, Throwable t) {
            super(msg, t);
        }

        public String getOAuth2ErrorCode() {
            return "server_error";
        }

        public int getHttpErrorCode() {
            return 500;
        }
    }

    private static class ForbiddenException extends ReOAuth2Exception {
        public ForbiddenException(String msg, Throwable t) {
            super(msg, t);
        }

        public String getOAuth2ErrorCode() {
            return "access_denied";
        }

        public int getHttpErrorCode() {
            return 403;
        }
    }
}
