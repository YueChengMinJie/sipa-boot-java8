package com.sipa.boot.java8.iot.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.guava.CaffeinatedGuava;
import com.sipa.boot.java8.iot.core.cluster.DefaultClusterManager;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterManager;
import com.sipa.boot.java8.iot.core.config.DefaultConfigStorageManager;
import com.sipa.boot.java8.iot.core.config.base.IConfigStorageManager;
import com.sipa.boot.java8.iot.core.connector.base.IDeviceMessageConnector;
import com.sipa.boot.java8.iot.core.device.DefaultDeviceRegistry;
import com.sipa.boot.java8.iot.core.device.base.IDeviceOperationBroker;
import com.sipa.boot.java8.iot.core.device.base.IDeviceRegistry;
import com.sipa.boot.java8.iot.core.event.base.IEventBus;
import com.sipa.boot.java8.iot.core.property.IotProperties;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupports;
import com.sipa.boot.java8.iot.core.server.DefaultSendToDeviceMessageHandler;
import com.sipa.boot.java8.iot.core.server.base.IMessageHandler;
import com.sipa.boot.java8.iot.core.server.monitor.base.IGatewayServerMonitor;
import com.sipa.boot.java8.iot.core.server.session.DefaultDeviceSessionManager;
import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSessionManager;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
@Configuration
@ConditionalOnClass(IotProperties.class)
@ComponentScan(value = {"com.sipa.boot.java8.iot.core.**"})
@EnableConfigurationProperties({IotProperties.class})
public class IotAutoConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "vertx")
    public VertxOptions vertxOptions() {
        return new VertxOptions();
    }

    @Bean
    public Vertx vertx(VertxOptions vertxOptions) {
        return Vertx.vertx(vertxOptions);
    }

    @Bean(initMethod = "startup")
    public DefaultClusterManager clusterManager(IotProperties properties,
        ReactiveRedisTemplate<Object, Object> template) {
        return new DefaultClusterManager(properties.getClusterName(), properties.getServerId(), template);
    }

    @Bean
    public DefaultConfigStorageManager configStorageManager(IClusterManager clusterManager, IEventBus eventBus) {
        return new DefaultConfigStorageManager(clusterManager, eventBus,
            () -> CaffeinatedGuava.build(Caffeine.newBuilder()));
    }

    @Bean
    public DefaultDeviceRegistry deviceRegistry(IProtocolSupports supports, IClusterManager manager,
        IConfigStorageManager storageManager, IDeviceOperationBroker handler) {
        return new DefaultDeviceRegistry(supports, storageManager, manager, handler,
            CaffeinatedGuava.build(Caffeine.newBuilder()));
    }

    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public DefaultDeviceSessionManager deviceSessionManager(IotProperties properties, IGatewayServerMonitor monitor,
        IDeviceRegistry registry) {
        DefaultDeviceSessionManager sessionManager = new DefaultDeviceSessionManager();
        sessionManager.setGatewayServerMonitor(monitor);
        sessionManager.setRegistry(registry);
        Optional.ofNullable(properties.getTransportLimit()).ifPresent(sessionManager::setTransportLimits);
        return sessionManager;
    }

    @Bean(initMethod = "startup")
    public DefaultSendToDeviceMessageHandler defaultSendToDeviceMessageHandler(IotProperties properties,
        IDeviceSessionManager sessionManager, IDeviceRegistry registry, IMessageHandler messageHandler,
        @Autowired(required = false) IDeviceMessageConnector messageConnector) {
        return new DefaultSendToDeviceMessageHandler(properties.getServerId(), sessionManager, messageHandler, registry,
            messageConnector);
    }
}
