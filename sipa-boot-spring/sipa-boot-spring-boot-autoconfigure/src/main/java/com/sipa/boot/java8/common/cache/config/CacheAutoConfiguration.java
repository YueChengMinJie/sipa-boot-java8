package com.sipa.boot.java8.common.cache.config;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.google.common.base.Charsets;
import com.sipa.boot.java8.common.cache.property.CacheProperties;
import com.sipa.boot.java8.data.redis.config.RedisAutoConfiguration;

/**
 * @author zhouxiajie
 * @date 2019-06-03
 */
@EnableCaching
@Configuration
@ConditionalOnProperty(prefix = "sipa.boot.cache", name = "enabled", havingValue = "true")
@ConditionalOnClass({CacheProperties.class, RedisConnectionFactory.class})
@ConditionalOnBean(RedisConnectionFactory.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties({CacheProperties.class})
public class CacheAutoConfiguration {
    private final CacheProperties cacheProperties;

    public CacheAutoConfiguration(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory jedisConnectionFactory,
        ApplicationContext resourceLoader) {
        RedisCacheManager.RedisCacheManagerBuilder builder =
            RedisCacheManager.builder(RedisCacheWriter.lockingRedisCacheWriter(jedisConnectionFactory))
                .cacheDefaults(defaultCacheConfig(resourceLoader.getClassLoader()))
                .withInitialCacheConfigurations(
                    Collections.singletonMap("predefined", defaultCacheConfig(resourceLoader.getClassLoader())))
                .transactionAware();

        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
        }

        return builder.build();
    }

    private RedisCacheConfiguration defaultCacheConfig(ClassLoader classLoader) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheName -> "( ͡° ᴥ ͡°)" + cacheName + "( ͡° _ ͡°):")
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer(Charsets.UTF_8)))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new JdkSerializationRedisSerializer(classLoader)));

        if (cacheProperties.getTimeToLive() != null) {
            config = config.entryTtl(cacheProperties.getTimeToLive());
        }

        if (!cacheProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }

        return config;
    }
}
