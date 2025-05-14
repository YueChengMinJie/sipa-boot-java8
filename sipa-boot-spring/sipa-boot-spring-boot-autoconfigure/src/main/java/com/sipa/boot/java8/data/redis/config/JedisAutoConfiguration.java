package com.sipa.boot.java8.data.redis.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import com.sipa.boot.java8.data.redis.property.RedisProperties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author zhouxiajie
 * @date 2019-07-11
 */
@Configuration
@ConditionalOnClass({GenericObjectPool.class, JedisConnection.class, Jedis.class, RedisProperties.class})
@AutoConfigureAfter(value = {RedisProperties.class})
public class JedisAutoConfiguration {
    private final RedisProperties properties;

    @Autowired
    public JedisAutoConfiguration(RedisProperties redisProperties) {
        this.properties = redisProperties;
    }

    @Bean
    @Primary
    public JedisConnectionFactory redisConnectionFactory() {
        return createJedisConnectionFactory();
    }

    private JedisConnectionFactory createJedisConnectionFactory() {
        JedisClientConfiguration clientConfiguration = getJedisClientConfiguration();
        return new JedisConnectionFactory(getStandaloneConfig(), clientConfiguration);
    }

    private JedisClientConfiguration getJedisClientConfiguration() {
        JedisClientConfiguration.JedisClientConfigurationBuilder builder =
            applyProperties(JedisClientConfiguration.builder());

        applyPooling(builder);

        return builder.build();
    }

    private JedisClientConfiguration.JedisClientConfigurationBuilder
        applyProperties(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        if (this.properties.isSsl()) {
            builder.useSsl();
        }

        builder.readTimeout(Duration.ofMillis(this.properties.getReadTimeout()))
            .connectTimeout(Duration.ofMillis(this.properties.getConnectTimeout()));

        return builder;
    }

    private void applyPooling(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        builder.usePooling().poolConfig(jedisPoolConfig());
    }

    private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();

        // 最小空闲连接数
        config.setMinIdle(this.properties.getMinIdle());
        // 最大空闲连接数
        config.setMaxIdle(this.properties.getMaxIdle());
        // 最大连接数
        config.setMaxTotal(this.properties.getMaxTotal());
        // 连接检测
        config.setTestOnBorrow(true);
        // 空闲检测
        config.setTestWhileIdle(true);
        config.setTimeBetweenEvictionRunsMillis(TimeUnit.SECONDS.toMillis(30));
        config.setMinEvictableIdleTimeMillis(TimeUnit.SECONDS.toMillis(60));
        config.setNumTestsPerEvictionRun(-1);

        return config;
    }

    private RedisStandaloneConfiguration getStandaloneConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(this.properties.getHost());
        config.setPort(this.properties.getPort());
        config.setPassword(RedisPassword.of(this.properties.getPassword()));
        config.setDatabase(this.properties.getDb());
        return config;
    }
}
