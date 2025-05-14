package com.sipa.boot.java8.common.async.config;

import java.time.Duration;
import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import com.sipa.boot.java8.common.async.handler.GlobalAsyncExceptionHandler;
import com.sipa.boot.java8.common.async.property.AsyncProperties;

/**
 * @author 甘华根
 * @since 2020/9/18 10:26
 */
@Configuration
@ConditionalOnClass({AsyncProperties.class})
@EnableAsync
@ComponentScan(value = {"com.sipa.boot.java8.**.common.async.**"})
public class AsyncAutoConfiguration implements AsyncConfigurer {
    private final AsyncProperties properties;

    private final ObjectProvider<TaskExecutorCustomizer> taskExecutorCustomizers;

    private final ObjectProvider<TaskDecorator> taskDecorator;

    private final GlobalAsyncExceptionHandler globalAsyncExceptionHandler;

    public AsyncAutoConfiguration(AsyncProperties asyncProperties,
        ObjectProvider<TaskExecutorCustomizer> taskExecutorCustomizers, ObjectProvider<TaskDecorator> taskDecorator,
        GlobalAsyncExceptionHandler globalAsyncExceptionHandler) {
        this.properties = asyncProperties;
        this.taskExecutorCustomizers = taskExecutorCustomizers;
        this.taskDecorator = taskDecorator;
        this.globalAsyncExceptionHandler = globalAsyncExceptionHandler;
    }

    @Override
    @Bean
    public Executor getAsyncExecutor() {
        return createTskExecutorBuilder().build();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return globalAsyncExceptionHandler;
    }

    private TaskExecutorBuilder createTskExecutorBuilder() {
        TaskExecutorBuilder builder = new TaskExecutorBuilder();
        builder = builder.queueCapacity(this.properties.getQueueCapacity());
        builder = builder.corePoolSize(this.properties.getCorePoolSize());
        builder = builder.maxPoolSize(this.properties.getMaxPoolSize());
        builder = builder.allowCoreThreadTimeOut(this.properties.getAllowCoreThreadTimeOut());
        builder = builder.keepAlive(Duration.ofSeconds(this.properties.getKeepAlive()));
        builder = builder.threadNamePrefix(this.properties.getThreadNamePrefix());
        builder = builder.customizers(this.taskExecutorCustomizers.orderedStream()::iterator);
        builder = builder.taskDecorator(this.taskDecorator.getIfUnique());
        return builder;
    }
}
