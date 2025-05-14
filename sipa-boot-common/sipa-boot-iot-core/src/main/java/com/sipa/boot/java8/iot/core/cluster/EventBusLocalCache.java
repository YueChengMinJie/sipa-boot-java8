package com.sipa.boot.java8.iot.core.cluster;

import java.util.Collection;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sipa.boot.java8.iot.core.cluster.base.AbstractLocalCache;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterCache;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterManager;
import com.sipa.boot.java8.iot.core.codec.Codecs;
import com.sipa.boot.java8.iot.core.codec.base.IEncoder;
import com.sipa.boot.java8.iot.core.event.base.IEventBus;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/3
 */
public class EventBusLocalCache<K, V> extends AbstractLocalCache<K, V> {
    private final IEventBus eventBus;

    private final String topicPrefix;

    private static final byte notifyData = (byte)1;

    private static final IEncoder<Byte> encoder = Codecs.lookup(byte.class);

    public EventBusLocalCache(String name, IEventBus eventBus, IClusterManager clusterManager) {
        this(name, eventBus, clusterManager, CacheBuilder.newBuilder().build());
    }

    public EventBusLocalCache(String name, IEventBus eventBus, IClusterManager clusterManager,
        Cache<K, Object> localCache) {
        this(name, eventBus, clusterManager.getCache(name), localCache);
    }

    public EventBusLocalCache(String name, IEventBus eventBus, IClusterCache<K, V> clusterCache,
        Cache<K, Object> localCache) {
        this(name, eventBus, clusterCache, localCache, true);
    }

    public EventBusLocalCache(String name, IEventBus eventBus, IClusterCache<K, V> clusterCache,
        Cache<K, Object> localCache, boolean cacheEmpty) {
        super(clusterCache, localCache, cacheEmpty);
        this.eventBus = eventBus;
        this.topicPrefix = "/_sys/cluster_cache/" + name;
    }

    @Override
    protected Mono<Void> onUpdate(K key, V value) {
        return eventBus.publish(topicPrefix + "/update/" + key, encoder, notifyData).then();
    }

    @Override
    protected Mono<Void> onRemove(K key) {
        return eventBus.publish(topicPrefix + "/remove/" + key, encoder, notifyData).then();
    }

    @Override
    protected Mono<Void> onRemove(Collection<? extends K> key) {
        return eventBus.publish(topicPrefix + "/remove/__all", encoder, notifyData).then();
    }

    @Override
    protected Mono<Void> onClear() {
        return eventBus.publish(topicPrefix + "/remove/__all", encoder, notifyData).then();
    }
}
