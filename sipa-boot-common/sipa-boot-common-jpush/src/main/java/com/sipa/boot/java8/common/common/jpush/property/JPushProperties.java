package com.sipa.boot.java8.common.common.jpush.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 极光推送参数
 *
 * @author sjm
 * @date 2021年11月30日 14点43分
 */
@ConfigurationProperties(prefix = "sipa.boot.jpush")
public class JPushProperties {
    /**
     * 是否开启
     */
    private boolean enabled;

    /**
     * masterSecret(从极光后台获得)
     */
    private String masterSecret;

    /**
     * appKey(从极光后台获得)
     */
    private String appKey;

    /**
     * 是否启用代理服务器
     */
    private boolean useProxy;

    /**
     * 代理服务器主机名或IP
     */
    private String proxyHost;

    /**
     * 代理服务器端口号
     */
    private int proxyPort;

    /**
     * 代理服务器用户名
     */
    private String proxyUsername;

    /**
     * 代理服务器密码
     */
    private String proxyPassword;

    /**
     * 重试时间间隔(毫秒)
     */
    private Long retryInterval = 500L;

    /**
     * 最大重试次数(0表示不重试)
     */
    private Integer retryMaxAttempts = 0;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getMasterSecret() {
        return masterSecret;
    }

    public void setMasterSecret(String masterSecret) {
        this.masterSecret = masterSecret;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public Long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(Long retryInterval) {
        this.retryInterval = retryInterval;
    }

    public Integer getRetryMaxAttempts() {
        return retryMaxAttempts;
    }

    public void setRetryMaxAttempts(Integer retryMaxAttempts) {
        this.retryMaxAttempts = retryMaxAttempts;
    }
}
