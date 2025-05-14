package com.sipa.boot.java8.iot.core.cluster;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.util.CollectionUtils;

import com.sipa.boot.java8.iot.core.cluster.base.IClusterCache;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultClusterCache<K, V> implements IClusterCache<K, V> {
    private final ReactiveHashOperations<Object, K, V> hash;

    private final String redisKey;

    public DefaultClusterCache(String redisKey, ReactiveRedisOperations<Object, Object> redis) {
        this(redisKey, redis.opsForHash());
    }

    private DefaultClusterCache(String redisKey, ReactiveHashOperations<Object, K, V> hash) {
        this.hash = hash;
        this.redisKey = redisKey;
    }

    @Override
    public Mono<V> get(K key) {
        return hash.get(redisKey, key);
    }

    @Override
    public Flux<Map.Entry<K, V>> get(Collection<K> key) {
        return hash.multiGet(redisKey, key).flatMapIterable(list -> {
            Object[] keyArr = key.toArray();
            List<Map.Entry<K, V>> entries = new ArrayList<>(keyArr.length);
            for (int i = 0; i < list.size(); i++) {
                entries.add(new RedisSimpleEntry((K)keyArr[i], list.get(i)));
            }
            return entries;
        });
    }

    @Override
    public Mono<Boolean> put(K key, V value) {
        return hash.put(redisKey, key, value);
    }

    @Override
    public Mono<Boolean> putIfAbsent(K key, V value) {
        return hash.putIfAbsent(redisKey, key, value);
    }

    @Override
    public Mono<V> getAndRemove(K key) {
        return hash.get(redisKey, key).flatMap(v -> remove(key).thenReturn(v));
    }

    @Override
    public Mono<Boolean> remove(K key) {
        return hash.remove(redisKey, key).thenReturn(true);
    }

    @Override
    public Mono<Boolean> remove(Collection<K> key) {
        return hash.remove(redisKey, key.toArray()).thenReturn(true);
    }

    @Override
    public Mono<Boolean> containsKey(K key) {
        return hash.hasKey(redisKey, key);
    }

    @Override
    public Flux<K> keys() {
        return hash.keys(redisKey);
    }

    @Override
    public Flux<V> values() {
        return hash.values(redisKey);
    }

    @Override
    public Mono<Boolean> putAll(Map<? extends K, ? extends V> multi) {
        if (CollectionUtils.isEmpty(multi)) {
            return Mono.just(true);
        }
        List<K> remove = multi.entrySet()
            .stream()
            .filter(e -> e.getValue() == null)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        if (remove.size() > 0) {
            Map<K, V> newTarget = new HashMap<>(multi);
            remove.forEach(newTarget::remove);
            return hash.remove(redisKey, remove.toArray()).then(hash.putAll(redisKey, newTarget));
        }
        return hash.putAll(redisKey, multi);
    }

    @Override
    public Mono<Integer> size() {
        return hash.size(redisKey).map(Number::intValue);
    }

    @Override
    public Flux<Map.Entry<K, V>> entries() {
        return hash.scan(redisKey).map(RedisHashEntry::new);
    }

    @Override
    public Mono<Void> clear() {
        return hash.delete(redisKey).then();
    }

    class RedisSimpleEntry implements Map.Entry<K, V> {
        K key;

        V value;

        RedisSimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = getValue();
            if (value == null) {
                remove(getKey()).subscribe();
            } else {
                put(getKey(), this.value = value).subscribe();
            }
            return old;
        }
    }

    class RedisHashEntry implements Map.Entry<K, V> {
        Map.Entry<K, V> entry;

        V value;

        RedisHashEntry(Map.Entry<K, V> entry) {
            this.entry = entry;
            this.value = entry.getValue();
        }

        @Override
        public K getKey() {
            return entry.getKey();
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = getValue();
            put(getKey(), this.value = value).subscribe();
            return old;
        }
    }
}
