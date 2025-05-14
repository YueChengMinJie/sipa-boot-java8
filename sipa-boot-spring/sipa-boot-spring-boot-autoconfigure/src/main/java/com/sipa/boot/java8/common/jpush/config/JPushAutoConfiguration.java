package com.sipa.boot.java8.common.jpush.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.common.common.jpush.component.JPushApi;
import com.sipa.boot.java8.common.common.jpush.property.JPushProperties;

/**
 * 自动配置类
 *
 * @author sjm
 * @date 2021年11月30日 14点43分
 */
@Configuration
@ConditionalOnClass(JPushProperties.class)
@EnableConfigurationProperties(JPushProperties.class)
@ComponentScan(basePackages = "com.sipa.boot.java8.**.common.jpush.**")
@ConditionalOnProperty(value = JPushAutoConfiguration.JPUSH_ENABLED, havingValue = "true")
public class JPushAutoConfiguration {
    public static final String JPUSH_ENABLED = "sipa.boot.jpush.enabled";

    private final JPushProperties jPushProperties;

    public JPushAutoConfiguration(final JPushProperties jPushProperties) {
        this.jPushProperties = jPushProperties;
    }

    @Bean
    public JPushApi jPushApi() {
        final JPushApi jPushApi = new JPushApi(jPushProperties);
        jPushApi.init();
        return jPushApi;
    }
}
