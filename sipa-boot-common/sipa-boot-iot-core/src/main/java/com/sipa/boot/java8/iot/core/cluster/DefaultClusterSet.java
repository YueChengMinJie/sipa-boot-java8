package com.sipa.boot.java8.iot.core.cluster;

import java.util.Collection;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveSetOperations;

import com.sipa.boot.java8.iot.core.cluster.base.IClusterSet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultClusterSet<V> implements IClusterSet<V> {
    private final ReactiveSetOperations<Object, Object> set;

    private final String redisKey;

    public DefaultClusterSet(String redisKey, ReactiveRedisOperations<Object, Object> redis) {
        this(redisKey, redis.opsForSet());
    }

    private DefaultClusterSet(String redisKey, ReactiveSetOperations<Object, Object> set) {
        this.set = set;
        this.redisKey = redisKey;
    }

    @Override
    public Mono<Boolean> add(V value) {
        return set.add(redisKey, value).thenReturn(true);
    }

    @Override
    public Mono<Boolean> add(Collection<V> values) {
        return set.add(redisKey, values.toArray()).thenReturn(true);
    }

    @Override
    public Mono<Boolean> remove(V value) {
        return set.remove(redisKey, value).thenReturn(true);
    }

    @Override
    public Mono<Boolean> remove(Collection<V> values) {
        return set.remove(redisKey, values.toArray()).thenReturn(true);
    }

    @Override
    public Flux<V> values() {
        return set.members(redisKey).map(v -> (V)v);
    }
}
