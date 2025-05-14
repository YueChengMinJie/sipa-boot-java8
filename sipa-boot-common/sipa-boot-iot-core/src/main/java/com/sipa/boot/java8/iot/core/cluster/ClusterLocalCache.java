package com.sipa.boot.java8.iot.core.cluster;

import java.util.Collection;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sipa.boot.java8.iot.core.cluster.base.AbstractLocalCache;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterCache;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterManager;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterTopic;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class ClusterLocalCache<K, V> extends AbstractLocalCache<K, V> {
    private final IClusterTopic<K> clearTopic;

    public ClusterLocalCache(String name, IClusterManager clusterManager) {
        this(name, clusterManager, clusterManager.getCache(name), CacheBuilder.newBuilder().build());
    }

    public ClusterLocalCache(String name, IClusterManager clusterManager, IClusterCache<K, V> clusterCache,
        Cache<K, Object> localCache) {
        super(clusterCache, localCache);
        this.clearTopic = clusterManager.getTopic("_local_cache_modify:".concat(name));
    }

    @Override
    protected Mono<Void> onUpdate(K key, V value) {
        return clearTopic.publish(Mono.just(key)).then();
    }

    @Override
    protected Mono<Void> onRemove(K key) {
        return clearTopic.publish(Mono.just(key)).then();
    }

    @Override
    protected Mono<Void> onRemove(Collection<? extends K> key) {
        return clearTopic.publish(Flux.fromIterable(key)).then();
    }

    @Override
    protected Mono<Void> onClear() {
        return clearTopic.publish(Mono.just((K)"__all")).then();
    }
}
