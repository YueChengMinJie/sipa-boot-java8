package com.sipa.boot.java8.iot.core.config;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sipa.boot.java8.common.archs.cache.Caches;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.cluster.EventBusLocalCache;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterManager;
import com.sipa.boot.java8.iot.core.config.base.IConfigStorage;
import com.sipa.boot.java8.iot.core.config.base.IConfigStorageManager;
import com.sipa.boot.java8.iot.core.event.Subscription;
import com.sipa.boot.java8.iot.core.event.base.IEventBus;

import io.netty.util.ReferenceCountUtil;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/3
 */
public class DefaultConfigStorageManager implements IConfigStorageManager {
    private static final Log log = LogFactory.get(DefaultConfigStorageManager.class);

    private final ConcurrentMap<String, ClusterConfigStorage> cache;

    private final Supplier<Cache<String, Object>> cacheSupplier;

    private final Function<String, ClusterConfigStorage> storageBuilder;

    public DefaultConfigStorageManager(IClusterManager clusterManager, IEventBus eventBus) {
        this(clusterManager, eventBus, () -> CacheBuilder.newBuilder().build());
    }

    public DefaultConfigStorageManager(IClusterManager clusterManager, IEventBus eventBus,
        Supplier<Cache<String, Object>> supplier) {
        this(clusterManager, eventBus, supplier, true);
    }

    public DefaultConfigStorageManager(IClusterManager clusterManager, IEventBus eventBus,
        Supplier<Cache<String, Object>> supplier, boolean cacheEmpty) {
        this.cache = Caches.newCache();
        this.cacheSupplier = supplier;
        this.storageBuilder = id -> new ClusterConfigStorage(
            new EventBusLocalCache<>(id, eventBus, clusterManager.getCache(id), cacheSupplier.get(), cacheEmpty));

        eventBus.subscribe(Subscription.builder()
            .subscriberId("event-bus-storage-listener")
            .topics("/_sys/cluster_cache/*/*/*")
            .justBroker()
            .build()).subscribe(payload -> {
                try {
                    Map<String, String> vars = payload.getTopicVars("/_sys/cluster_cache/{name}/{type}/{key}");

                    ClusterConfigStorage storage = cache.get(vars.get("name"));
                    if (storage != null) {
                        log.trace("clear local cache :{}", vars);
                        EventBusLocalCache eventBusLocalCache = ((EventBusLocalCache)storage.getCache());
                        eventBusLocalCache.clearLocalCache(vars.get("key"));
                    } else {
                        log.trace("ignore clear local cache :{}", vars);
                    }
                } catch (Throwable error) {
                    log.warn("clearn local cache error", error);
                } finally {
                    ReferenceCountUtil.safeRelease(payload);
                }
            });
    }

    @Override
    public Mono<IConfigStorage> getStorage(String id) {
        return Mono.fromSupplier(() -> cache.computeIfAbsent(id, storageBuilder));
    }
}
