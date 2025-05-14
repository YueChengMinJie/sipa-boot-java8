package com.sipa.boot.java8.common.archs.lru;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhouxiajie
 * @date 2020/12/14
 */
public class LruCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxCacheSize;

    public LruCache(int maxCacheSize) {
        super((int)Math.ceil(maxCacheSize / 0.75) + 1, 0.75f, true);
        this.maxCacheSize = maxCacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxCacheSize;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<K, V> entry : entrySet()) {
            sb.append(String.format("%s:%s ", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}
