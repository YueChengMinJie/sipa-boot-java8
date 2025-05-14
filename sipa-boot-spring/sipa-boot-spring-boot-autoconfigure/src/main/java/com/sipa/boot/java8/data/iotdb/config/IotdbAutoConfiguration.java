package com.sipa.boot.java8.data.iotdb.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.data.iotdb.property.IotdbProperties;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
@Configuration
@ConditionalOnClass(IotdbProperties.class)
@ComponentScan(value = {"com.sipa.boot.java8.data.iotdb.**"})
@EnableConfigurationProperties({IotdbProperties.class})
public class IotdbAutoConfiguration {

}
