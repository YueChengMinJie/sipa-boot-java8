package com.sipa.boot.java8.iot.core.util;

import java.util.Optional;
import java.util.function.Function;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

import io.netty.util.Recycler;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class RecyclerUtils {
    private static final Log log = LogFactory.get(RecyclerUtils.class);

    public static <T> Recycler<T> newRecycler(Class<T> type, Function<Recycler.Handle<T>, T> objectSupplier,
        int defaultRatio) {
        int maxCapacityPerThread = getPoolConfig(type, "maxCapacityPerThread").map(Integer::parseInt).orElse(4096);

        int maxSharedCapacityFactor = getPoolConfig(type, "maxSharedCapacityFactor").map(Integer::parseInt).orElse(2);

        int maxDelayedQueuesPerThread = getPoolConfig(type, "maxDelayedQueuesPerThread").map(Integer::parseInt)
            .orElse(Runtime.getRuntime().availableProcessors() * 2);

        int ratio = getPoolConfig(type, "ratio").map(Integer::parseInt).orElse(defaultRatio);

        log.debug("-D{}: {}", getConfigName(type, "maxCapacityPerThread"), maxCapacityPerThread);
        log.debug("-D{}: {}", getConfigName(type, "maxSharedCapacityFactor"), maxSharedCapacityFactor);
        log.debug("-D{}: {}", getConfigName(type, "maxDelayedQueuesPerThread"), maxDelayedQueuesPerThread);
        log.debug("-D{}: {}", getConfigName(type, "ratio"), ratio);

        return new Recycler<T>(maxCapacityPerThread, maxSharedCapacityFactor, ratio, maxDelayedQueuesPerThread) {
            @Override
            protected T newObject(Handle<T> handle) {
                return objectSupplier.apply(handle);
            }
        };
    }

    public static <T> Recycler<T> newRecycler(Class<T> type, Function<Recycler.Handle<T>, T> objectSupplier) {
        return newRecycler(type, objectSupplier, 8);
    }

    private static Optional<String> getPoolConfig(Class<?> type, String key) {
        return Optional.ofNullable(System.getProperty(getConfigName(type, key)));
    }

    private static String getConfigName(Class<?> type, String key) {
        return (type.getName() + ".pool." + key).replace("$$", ".").replace("$", ".");
    }
}
