package com.sipa.boot.java8.data.mongodb.property;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author feizhihao
 * @date 2019/12/13 9:39 上午
 */
@Component
@ConfigurationProperties(prefix = "sipa.boot.mongodb")
public class MongodbProperties {
    private List<String> address;

    private String replicaSet;

    private String database;

    private String username;

    private String password;

    private String authenticationDatabase;

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    private Integer minConnectionsPerHost = 0;

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    private Integer maxConnectionsPerHost = 100;

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    private Integer threadsAllowedToBlockForConnectionMultiplier = 5;

    private Integer serverSelectionTimeout = 30000;

    private Integer maxWaitTime = 120000;

    private Integer maxConnectionIdleTime = 0;

    private Integer maxConnectionLifeTime = 0;

    private Integer connectTimeout = 10000;

    private Integer readTimeout = 0;

    private Boolean socketKeepAlive = false;

    private Boolean sslEnabled = false;

    private Boolean invalidHostNameAllowed = false;

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    private Boolean alwaysUseMBeans = false;

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    private Integer heartbeatConnectTimeout = 20000;

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    private Integer heartbeatSocketTimeout = 20000;

    private Integer minHeartbeatFrequency = 500;

    private Integer heartbeatFrequency = 10000;

    private Integer localThreshold = 15;

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public String getReplicaSet() {
        return replicaSet;
    }

    public void setReplicaSet(String replicaSet) {
        this.replicaSet = replicaSet;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    public Integer getMinConnectionsPerHost() {
        return minConnectionsPerHost;
    }

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    public void setMinConnectionsPerHost(Integer minConnectionsPerHost) {
        this.minConnectionsPerHost = minConnectionsPerHost;
    }

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    public Integer getMaxConnectionsPerHost() {
        return maxConnectionsPerHost;
    }

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    public void setMaxConnectionsPerHost(Integer maxConnectionsPerHost) {
        this.maxConnectionsPerHost = maxConnectionsPerHost;
    }

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    public Integer getThreadsAllowedToBlockForConnectionMultiplier() {
        return threadsAllowedToBlockForConnectionMultiplier;
    }

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    public void setThreadsAllowedToBlockForConnectionMultiplier(Integer threadsAllowedToBlockForConnectionMultiplier) {
        this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
    }

    public Integer getServerSelectionTimeout() {
        return serverSelectionTimeout;
    }

    public void setServerSelectionTimeout(Integer serverSelectionTimeout) {
        this.serverSelectionTimeout = serverSelectionTimeout;
    }

    public Integer getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(Integer maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public Integer getMaxConnectionIdleTime() {
        return maxConnectionIdleTime;
    }

    public void setMaxConnectionIdleTime(Integer maxConnectionIdleTime) {
        this.maxConnectionIdleTime = maxConnectionIdleTime;
    }

    public Integer getMaxConnectionLifeTime() {
        return maxConnectionLifeTime;
    }

    public void setMaxConnectionLifeTime(Integer maxConnectionLifeTime) {
        this.maxConnectionLifeTime = maxConnectionLifeTime;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Boolean getSocketKeepAlive() {
        return socketKeepAlive;
    }

    public void setSocketKeepAlive(Boolean socketKeepAlive) {
        this.socketKeepAlive = socketKeepAlive;
    }

    public Boolean getSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(Boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public Boolean getInvalidHostNameAllowed() {
        return invalidHostNameAllowed;
    }

    public void setInvalidHostNameAllowed(Boolean invalidHostNameAllowed) {
        this.invalidHostNameAllowed = invalidHostNameAllowed;
    }

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    public Boolean getAlwaysUseMBeans() {
        return alwaysUseMBeans;
    }

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    public void setAlwaysUseMBeans(Boolean alwaysUseMBeans) {
        this.alwaysUseMBeans = alwaysUseMBeans;
    }

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    public Integer getHeartbeatConnectTimeout() {
        return heartbeatConnectTimeout;
    }

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    public void setHeartbeatConnectTimeout(Integer heartbeatConnectTimeout) {
        this.heartbeatConnectTimeout = heartbeatConnectTimeout;
    }

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    public Integer getHeartbeatSocketTimeout() {
        return heartbeatSocketTimeout;
    }

    /**
     * Deprecated spring data mongodb 3.0
     */
    @Deprecated
    public void setHeartbeatSocketTimeout(Integer heartbeatSocketTimeout) {
        this.heartbeatSocketTimeout = heartbeatSocketTimeout;
    }

    public Integer getMinHeartbeatFrequency() {
        return minHeartbeatFrequency;
    }

    public void setMinHeartbeatFrequency(Integer minHeartbeatFrequency) {
        this.minHeartbeatFrequency = minHeartbeatFrequency;
    }

    public Integer getHeartbeatFrequency() {
        return heartbeatFrequency;
    }

    public void setHeartbeatFrequency(Integer heartbeatFrequency) {
        this.heartbeatFrequency = heartbeatFrequency;
    }

    public Integer getLocalThreshold() {
        return localThreshold;
    }

    public void setLocalThreshold(Integer localThreshold) {
        this.localThreshold = localThreshold;
    }

    public String getAuthenticationDatabase() {
        return authenticationDatabase;
    }

    public void setAuthenticationDatabase(String authenticationDatabase) {
        this.authenticationDatabase = authenticationDatabase;
    }
}
