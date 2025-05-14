package com.sipa.boot.java8.data.redis.config;

import java.util.List;
import java.util.Objects;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.sipa.boot.java8.data.redis.annotaion.Topic;
import com.sipa.boot.java8.data.redis.property.RedisProperties;
import com.sipa.boot.java8.data.redis.service.IRedisMessageSubscriber;

/**
 * @author zhouxiajie
 * @date 2019-01-22
 */
@EnableCaching
@Configuration
@ConditionalOnClass(RedisProperties.class)
@ComponentScan(value = {"com.sipa.boot.java8.data.redis.**"})
@AutoConfigureAfter(value = {JedisAutoConfiguration.class})
public class RedisAutoConfiguration {
    private final RedisProperties properties;

    public RedisAutoConfiguration(RedisProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
        StringRedisSerializer stringRedisSerializer) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setDefaultSerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(stringRedisSerializer);
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(false);
        return template;
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    @ConditionalOnProperty(prefix = "sipa.boot.redis", name = "sub", havingValue = "true")
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory jedisConnectionFactory,
        List<IRedisMessageSubscriber> redisMessageSubscriberList) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        // 遍历所有实现类
        redisMessageSubscriberList.forEach(redisMessageSubscriber -> {
            // 获取实现类的所有注解
            Topic topic = redisMessageSubscriber.getClass().getAnnotation(Topic.class);
            if (null != topic) {
                for (String val : topic.value()) {
                    // 将注解及实现类注册到redis监听器容器中
                    container.addMessageListener(new MessageListenerAdapter(redisMessageSubscriber),
                        new ChannelTopic(Objects.requireNonNull(this.properties.getSubTopicMap().get(val))));
                }
            } else {
                // 当未配置topic注解时，则抛出异常
                throw new RuntimeException(redisMessageSubscriber.getClass().getName() + " miss annotation [Topic]");
            }
        });
        return container;
    }
}
