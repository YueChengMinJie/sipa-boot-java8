package com.sipa.boot.java8.common.archs.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.CacheBuilder;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class Caches {
    private static final Supplier<ConcurrentMap<Object, Object>> CACHE_SUPPLIER;

    private static boolean caffeinePresent() {
        if (Boolean.getBoolean("sipa.boot.cache.caffeine.disabled")) {
            return false;
        }
        try {
            Class.forName("com.github.benmanes.caffeine.cache.Cache");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean guavaPresent() {
        return !Boolean.getBoolean("sipa.boot.cache.guava.disabled");
    }

    private static ConcurrentMap<Object, Object> createCaffeine() {
        return Caffeine.newBuilder().build().asMap();
    }

    private static ConcurrentMap<Object, Object> createGuava() {
        return CacheBuilder.newBuilder().build().asMap();
    }

    static {
        if (caffeinePresent()) {
            CACHE_SUPPLIER = Caches::createCaffeine;
        } else if (guavaPresent()) {
            CACHE_SUPPLIER = Caches::createGuava;
        } else {
            CACHE_SUPPLIER = ConcurrentHashMap::new;
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> ConcurrentMap<K, V> newCache() {
        return (ConcurrentMap<K, V>)CACHE_SUPPLIER.get();
    }
}
