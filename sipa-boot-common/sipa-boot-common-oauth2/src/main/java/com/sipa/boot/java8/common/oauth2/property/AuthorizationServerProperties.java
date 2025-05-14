package com.sipa.boot.java8.common.oauth2.property;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.oauth2.enumerate.ELoginStrategy;

/**
 * @author zhouxiajie
 * @date 2019-01-23
 */
@ConfigurationProperties(prefix = "sipa.boot.security")
@Component
public class AuthorizationServerProperties {
    private ELoginStrategy loginStrategy;

    private String clientId;

    private String clientSecret;

    private List<String> authorizedGrantTypes;

    private List<String> scopes;

    private int accessTokenValiditySeconds;

    private int refreshTokenValiditySeconds;

    private String jksPath;

    private String jksPassword;

    private String jksAlias;

    public ELoginStrategy getLoginStrategy() {
        return loginStrategy;
    }

    public void setLoginStrategy(ELoginStrategy loginStrategy) {
        this.loginStrategy = loginStrategy;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public List<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(List<String> authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public int getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public int getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public String getJksPath() {
        return jksPath;
    }

    public void setJksPath(String jksPath) {
        this.jksPath = jksPath;
    }

    public String getJksPassword() {
        return jksPassword;
    }

    public void setJksPassword(String jksPassword) {
        this.jksPassword = jksPassword;
    }

    public String getJksAlias() {
        return jksAlias;
    }

    public void setJksAlias(String jksAlias) {
        this.jksAlias = jksAlias;
    }
}
