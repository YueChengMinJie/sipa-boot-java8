package com.sipa.boot.java8.common.oauth2.translator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

import com.sipa.boot.java8.common.oauth2.exception.custom.base.BaseCustomOAuth2Exception;
import com.sipa.boot.java8.common.oauth2.exception.oauth.*;

/**
 * @author fzh
 */
public class AuthorizationServerWebResponseExceptionTranslator
    implements WebResponseExceptionTranslator<OAuth2Exception> {
    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception exception) {
        if (exception instanceof BadClientCredentialsException) {
            BadClientCredentialsException oauth2Exception = (BadClientCredentialsException)exception;
            return ResponseEntity.status(oauth2Exception.getHttpErrorCode())
                .body(new CustomBadClientCredentialsException());
        } else if (exception instanceof InsufficientScopeException) {
            InsufficientScopeException oauth2Exception = (InsufficientScopeException)exception;
            return ResponseEntity.status(oauth2Exception.getHttpErrorCode())
                .body(new CustomInsufficientScopeException(exception.getMessage()));
        } else if (exception instanceof UnauthorizedUserException) {
            UnauthorizedUserException oauth2Exception = (UnauthorizedUserException)exception;
            return ResponseEntity.status(oauth2Exception.getHttpErrorCode())
                .body(new CustomUnauthorizedUserException(exception.getMessage()));
        } else if (exception instanceof InvalidClientException) {
            InvalidClientException oAuth2Exception = (InvalidClientException)exception;
            return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
                .body(new CustomInvalidClientException(oAuth2Exception.getMessage()));
        } else if (exception instanceof UnauthorizedClientException) {
            UnauthorizedClientException oAuth2Exception = (UnauthorizedClientException)exception;
            return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
                .body(new CustomUnauthorizedClientException(oAuth2Exception.getMessage()));
        } else if (exception instanceof InvalidGrantException) {
            InvalidGrantException oAuth2Exception = (InvalidGrantException)exception;
            return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
                .body(new CustomInvalidGrantException(oAuth2Exception.getMessage()));
        } else if (exception instanceof InvalidScopeException) {
            InvalidScopeException oAuth2Exception = (InvalidScopeException)exception;
            return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
                .body(new CustomInvalidScopeException(oAuth2Exception.getMessage()));
        } else if (exception instanceof InvalidTokenException) {
            InvalidTokenException oAuth2Exception = (InvalidTokenException)exception;
            return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
                .body(new CustomInvalidTokenException(oAuth2Exception.getMessage()));
        } else if (exception instanceof InvalidRequestException) {
            InvalidRequestException oAuth2Exception = (InvalidRequestException)exception;
            return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
                .body(new CustomInvalidRequestException(oAuth2Exception.getMessage()));
        } else if (exception instanceof RedirectMismatchException) {
            RedirectMismatchException oAuth2Exception = (RedirectMismatchException)exception;
            return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
                .body(new CustomRedirectMismatchException(oAuth2Exception.getMessage()));
        } else if (exception instanceof UnsupportedGrantTypeException) {
            UnsupportedGrantTypeException oAuth2Exception = (UnsupportedGrantTypeException)exception;
            return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
                .body(new CustomUnsupportedGrantTypeException(oAuth2Exception.getMessage()));
        } else if (exception instanceof UnsupportedResponseTypeException) {
            UnsupportedResponseTypeException oAuth2Exception = (UnsupportedResponseTypeException)exception;
            return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
                .body(new CustomUnsupportedResponseTypeException(oAuth2Exception.getMessage()));
        } else if (exception instanceof UserDeniedAuthorizationException) {
            UserDeniedAuthorizationException oAuth2Exception = (UserDeniedAuthorizationException)exception;
            return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
                .body(new CustomUserDeniedAuthorizationException(oAuth2Exception.getMessage()));
        } else if (exception instanceof InternalAuthenticationServiceException) {
            Throwable cause = exception.getCause();
            if (BaseCustomOAuth2Exception.class.isAssignableFrom(cause.getClass())) {
                BaseCustomOAuth2Exception ex = (BaseCustomOAuth2Exception)cause;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomInternalAuthenticationServiceException(ex.getOAuth2ErrorCode(), ex.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomOAuth2Exception(exception.getMessage()));
            }
        } else if (exception instanceof AuthenticationException) {
            AuthenticationException authenticationException = (AuthenticationException)exception;
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CustomOAuth2Exception(authenticationException.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CustomOAuth2Exception(exception.getMessage()));
    }
}
