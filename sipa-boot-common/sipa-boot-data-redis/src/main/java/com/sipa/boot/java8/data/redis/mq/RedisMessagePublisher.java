package com.sipa.boot.java8.data.redis.mq;

import java.util.Objects;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.data.redis.property.RedisProperties;
import com.sipa.boot.java8.data.redis.service.IRedisMessagePublisher;

/**
 * @author zhouxiajie
 * @date 2019-03-16
 */
@ConditionalOnProperty(prefix = "sipa.boot.redis", name = "pub", havingValue = "true")
@Component
public class RedisMessagePublisher implements IRedisMessagePublisher {
    private static final Log LOGGER = LogFactory.get(RedisMessagePublisher.class);

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisProperties redisProperties;

    public RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate, RedisProperties redisProperties) {
        this.redisTemplate = redisTemplate;
        this.redisProperties = redisProperties;
    }

    @Override
    public void publish(String message) {
        String pubDefaultTopic = Objects.requireNonNull(redisProperties.getPubDefaultTopic());
        publish(pubDefaultTopic, message);
    }

    @Override
    public void publish(String topic, String message) {
        String realTopic = Objects.requireNonNull(this.redisProperties.getPubTopicMap().get(topic));
        LOGGER.info("Publish redis message [{}], topic [{}]", message, realTopic);
        redisTemplate.convertAndSend(realTopic, message);
    }
}
