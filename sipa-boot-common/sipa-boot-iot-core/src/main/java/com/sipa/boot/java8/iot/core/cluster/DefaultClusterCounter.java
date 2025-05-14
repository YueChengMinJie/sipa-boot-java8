package com.sipa.boot.java8.iot.core.cluster;

import java.math.BigDecimal;

import org.springframework.data.redis.core.ReactiveRedisOperations;

import com.sipa.boot.java8.iot.core.cluster.base.IClusterCounter;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultClusterCounter implements IClusterCounter {
    private final ReactiveRedisOperations<String, String> redis;

    private final String redisKey;

    public DefaultClusterCounter(ReactiveRedisOperations<String, String> redis, String redisKey) {
        this.redis = redis;
        this.redisKey = redisKey;
    }

    @Override
    public Mono<Double> increment(double delta) {
        return redis.opsForValue().increment(redisKey, delta);
    }

    @Override
    public Mono<Double> get() {
        return redis.opsForValue().get(redisKey).map(BigDecimal::new).map(Number::doubleValue).defaultIfEmpty(0D);
    }

    @Override
    public Mono<Double> set(double value) {
        return getAndSet(value);
    }

    @Override
    public Mono<Double> setAndGet(double value) {
        return redis.opsForValue().set(redisKey, String.valueOf(value)).thenReturn(value);
    }

    @Override
    public Mono<Double> getAndSet(double value) {
        return redis.opsForValue()
            .getAndSet(redisKey, String.valueOf(value))
            .map(BigDecimal::new)
            .map(Number::doubleValue)
            .defaultIfEmpty(0D);
    }
}
