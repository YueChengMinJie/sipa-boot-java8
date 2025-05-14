package com.sipa.boot.java8.common.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.common.api.property.ApiProperty;

/**
 * @author zhouxiajie
 * @date 2019-01-22
 */
@Configuration
@ConditionalOnClass(ApiProperty.class)
@ComponentScan(value = {"com.sipa.boot.java8.**.api.**"})
@EnableConfigurationProperties(ApiProperty.class)
public class ApiAutoConfiguration {
    // noop
}
