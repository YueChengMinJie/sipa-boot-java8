package com.sipa.boot.java8.common.threadpool.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author sunyukun
 * @since 2019/5/27 19:27
 */
@ConfigurationProperties(prefix = "sipa.boot.thread-pool")
@Component
public class ThreadPoolProperties {
    private boolean defaultEnabled;

    private String defaultPoolName;

    private int defaultCorePoolSize;

    private int defaultMaximumPoolSize;

    private long defaultKeepAliveTime;

    private boolean scheduledEnabled;

    private int scheduledCorePoolSize;

    private String scheduledPoolName;

    public boolean isDefaultEnabled() {
        return defaultEnabled;
    }

    public void setDefaultEnabled(boolean defaultEnabled) {
        this.defaultEnabled = defaultEnabled;
    }

    public String getDefaultPoolName() {
        return defaultPoolName;
    }

    public void setDefaultPoolName(String defaultPoolName) {
        this.defaultPoolName = defaultPoolName;
    }

    public int getDefaultCorePoolSize() {
        return defaultCorePoolSize;
    }

    public void setDefaultCorePoolSize(int defaultCorePoolSize) {
        this.defaultCorePoolSize = defaultCorePoolSize;
    }

    public int getDefaultMaximumPoolSize() {
        return defaultMaximumPoolSize;
    }

    public void setDefaultMaximumPoolSize(int defaultMaximumPoolSize) {
        this.defaultMaximumPoolSize = defaultMaximumPoolSize;
    }

    public long getDefaultKeepAliveTime() {
        return defaultKeepAliveTime;
    }

    public void setDefaultKeepAliveTime(long defaultKeepAliveTime) {
        this.defaultKeepAliveTime = defaultKeepAliveTime;
    }

    public boolean isScheduledEnabled() {
        return scheduledEnabled;
    }

    public void setScheduledEnabled(boolean scheduledEnabled) {
        this.scheduledEnabled = scheduledEnabled;
    }

    public int getScheduledCorePoolSize() {
        return scheduledCorePoolSize;
    }

    public void setScheduledCorePoolSize(int scheduledCorePoolSize) {
        this.scheduledCorePoolSize = scheduledCorePoolSize;
    }

    public String getScheduledPoolName() {
        return scheduledPoolName;
    }

    public void setScheduledPoolName(String scheduledPoolName) {
        this.scheduledPoolName = scheduledPoolName;
    }
}
