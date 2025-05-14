package com.sipa.boot.java8.common.threadpool.config;

import java.util.concurrent.*;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sipa.boot.java8.common.threadpool.property.ThreadPoolProperties;

/**
 * @author zhouxiajie
 * @date 2019-01-22
 */
@Configuration
@ConditionalOnClass(ThreadPoolProperties.class)
@EnableConfigurationProperties({ThreadPoolProperties.class})
public class ThreadPoolAutoConfiguration {
    private final ThreadPoolProperties threadPoolProperties;

    public ThreadPoolAutoConfiguration(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;
    }

    @Bean("sipaBootExecutorService")
    @ConditionalOnProperty(prefix = "sipa.boot.thread-pool", name = "defaultEnabled", havingValue = "true")
    public ExecutorService createExecutorService() {
        ThreadFactory factory =
            new ThreadFactoryBuilder().setNameFormat(threadPoolProperties.getDefaultPoolName() + "-%d").build();
        return new ThreadPoolExecutor(threadPoolProperties.getDefaultCorePoolSize(),
            threadPoolProperties.getDefaultMaximumPoolSize(), threadPoolProperties.getDefaultKeepAliveTime(),
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(200), factory);
    }

    @Bean("sipaBootScheduledExecutorService")
    @ConditionalOnProperty(prefix = "sipa.boot.thread-pool", name = "scheduledEnabled", havingValue = "true")
    public ScheduledExecutorService createScheduledExecutorService() {
        ThreadFactory factory =
            new ThreadFactoryBuilder().setNameFormat(threadPoolProperties.getScheduledPoolName() + "-%d").build();
        return new ScheduledThreadPoolExecutor(threadPoolProperties.getScheduledCorePoolSize(), factory);
    }
}
