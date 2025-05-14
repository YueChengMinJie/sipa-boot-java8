package com.sipa.boot.java8.data.redlock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.data.redlock.property.RedlockProperties;

/**
 * @author xuanyu
 * @date 2019-07-24
 */
@Configuration
@ComponentScan(value = {"com.sipa.boot.java8.data.redlock.**"})
@ConditionalOnClass(RedlockProperties.class)
@EnableConfigurationProperties(RedlockProperties.class)
public class RedlockAutoConfiguration {
    private final RedlockProperties redlockProperties;

    public RedlockAutoConfiguration(RedlockProperties redlockProperties) {
        this.redlockProperties = redlockProperties;
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();

        config.useSingleServer()
            .setAddress(redlockProperties.getAddress())
            .setPassword(redlockProperties.getPassword())
            .setDatabase(redlockProperties.getDb());

        return Redisson.create(config);
    }
}
