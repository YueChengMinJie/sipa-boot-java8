package com.sipa.boot.java8.common.cache.property;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhouxiajie
 * @date 2019-01-22
 */
@ConfigurationProperties(prefix = "sipa.boot.cache")
@Component
public class CacheProperties {
    /**
     * Comma-separated list of cache names to create if supported by the underlying cache manager. Usually, this
     * disables the ability to create additional caches on-the-fly.
     */
    private List<String> cacheNames = new ArrayList<>();

    /**
     * Entry expiration. By default the entries never expire.
     */
    private Duration timeToLive;

    /**
     * Allow caching null values.
     */
    private boolean cacheNullValues = false;

    public Duration getTimeToLive() {
        return this.timeToLive;
    }

    public void setTimeToLive(Duration timeToLive) {
        this.timeToLive = timeToLive;
    }

    public boolean isCacheNullValues() {
        return this.cacheNullValues;
    }

    public void setCacheNullValues(boolean cacheNullValues) {
        this.cacheNullValues = cacheNullValues;
    }

    public List<String> getCacheNames() {
        return cacheNames;
    }

    public void setCacheNames(List<String> cacheNames) {
        this.cacheNames = cacheNames;
    }
}
