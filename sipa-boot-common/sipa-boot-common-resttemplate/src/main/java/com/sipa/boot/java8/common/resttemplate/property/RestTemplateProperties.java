package com.sipa.boot.java8.common.resttemplate.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.log.property.CommonLoggingProperties;

/**
 * @author xiajiezhou
 * @date 2017/10/10
 */
@ConfigurationProperties(prefix = "sipa.boot.resttemplate")
@Component
public class RestTemplateProperties {
    private int maxConnTotal = 10;

    private int maxConnPerRoute = 5;

    private int connectTimeout = 5000;

    private int readTimeout = 296000;

    private int connRequestTimeout = 2000;

    private int retryCount = 3;

    private boolean requestSentRetryEnabled = false;

    @NestedConfigurationProperty
    private LoggingProperties logging = new LoggingProperties();

    public int getMaxConnTotal() {
        return maxConnTotal;
    }

    public void setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
    }

    public int getMaxConnPerRoute() {
        return maxConnPerRoute;
    }

    public void setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnRequestTimeout() {
        return connRequestTimeout;
    }

    public void setConnRequestTimeout(int connRequestTimeout) {
        this.connRequestTimeout = connRequestTimeout;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isRequestSentRetryEnabled() {
        return requestSentRetryEnabled;
    }

    public void setRequestSentRetryEnabled(boolean requestSentRetryEnabled) {
        this.requestSentRetryEnabled = requestSentRetryEnabled;
    }

    public LoggingProperties getLogging() {
        return logging;
    }

    public void setLogging(LoggingProperties logging) {
        this.logging = logging;
    }

    public static class LoggingProperties extends CommonLoggingProperties {
        @Override
        public String getBeforeMessagePrefix() {
            return "[Client] " + super.getBeforeMessagePrefix();
        }

        @Override
        public String getAfterMessagePrefix() {
            return "[Client] " + super.getAfterMessagePrefix();
        }
    }
}
