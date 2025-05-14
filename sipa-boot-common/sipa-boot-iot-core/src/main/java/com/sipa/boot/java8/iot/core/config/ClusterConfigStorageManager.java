package com.sipa.boot.java8.iot.core.config;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.sipa.boot.java8.iot.core.cluster.ClusterLocalCache;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterManager;
import com.sipa.boot.java8.iot.core.config.base.IConfigStorage;
import com.sipa.boot.java8.iot.core.config.base.IConfigStorageManager;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class ClusterConfigStorageManager implements IConfigStorageManager {
    private final IClusterManager clusterManager;

    private final Map<String, ClusterConfigStorage> cache = new ConcurrentHashMap<>();

    public ClusterConfigStorageManager(IClusterManager clusterManager) {
        this.clusterManager = clusterManager;
        clusterManager.getTopic("_local_cache_modify:*").subscribePattern().subscribe(msg -> {
            String[] type = msg.getTopic().split("[:]", 2);
            if (type.length <= 0) {
                return;
            }
            Optional.ofNullable(cache.get(type[1]))
                .ifPresent(store -> ((ClusterLocalCache)store.getCache()).clearLocalCache(msg.getMessage()));
        });
    }

    @Override
    public Mono<IConfigStorage> getStorage(String id) {
        return Mono.fromSupplier(() -> cache.computeIfAbsent(id,
            __ -> new ClusterConfigStorage(new ClusterLocalCache<>(id, clusterManager))));
    }
}
