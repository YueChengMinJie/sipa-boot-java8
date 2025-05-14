package com.sipa.boot.java8.data.redis.property;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhouxiajie
 * @date 2019-03-16
 */
@Component
public class RedisProperties {
    @Value("${sipa.boot.redis.host:redis.sipa.boot.com}")
    private String host;

    @Value("${sipa.boot.redis.port:6379}")
    private Integer port;

    @Value("${sipa.boot.redis.password:redis@admin}")
    private String password;

    @Value("${sipa.boot.redis.db:0}")
    private Integer db;

    @Value("${sipa.boot.redis.sub:}")
    private Boolean sub;

    @Value("#{${sipa.boot.redis.subTopicMap:null}}")
    private Map<String, String> subTopicMap;

    @Value("${sipa.boot.redis.pub:}")
    private Boolean pub;

    @Value("${sipa.boot.redis.pubDefaultTopic:}")
    private String pubDefaultTopic;

    @Value("#{${sipa.boot.redis.pubTopicMap:null}}")
    private Map<String, String> pubTopicMap;

    @Value("${sipa.boot.redis.pool.min-idle:0}")
    private Integer minIdle;

    @Value("${sipa.boot.redis.pool.max-idle:10}")
    private Integer maxIdle;

    @Value("${sipa.boot.redis.pool.max-total:200}")
    private Integer maxTotal;

    @Value("${sipa.boot.redis.pool.connect-timeout:0}")
    private Integer connectTimeout;

    @Value("${sipa.boot.redis.pool.read-timeout:5000}")
    private Integer readTimeout;

    @Value("${sipa.boot.redis.ssl:false}")
    private boolean ssl;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getPub() {
        return pub;
    }

    public void setPub(Boolean pub) {
        this.pub = pub;
    }

    public Boolean getSub() {
        return sub;
    }

    public void setSub(Boolean sub) {
        this.sub = sub;
    }

    public String getPubDefaultTopic() {
        return pubDefaultTopic;
    }

    public void setPubDefaultTopic(String pubDefaultTopic) {
        this.pubDefaultTopic = pubDefaultTopic;
    }

    public Integer getDb() {
        return db;
    }

    public void setDb(Integer db) {
        this.db = db;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
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

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public Map<String, String> getPubTopicMap() {
        return pubTopicMap;
    }

    public void setPubTopicMap(Map<String, String> pubTopicMap) {
        this.pubTopicMap = pubTopicMap;
    }

    public Map<String, String> getSubTopicMap() {
        return subTopicMap;
    }

    public void setSubTopicMap(Map<String, String> subTopicMap) {
        this.subTopicMap = subTopicMap;
    }
}
