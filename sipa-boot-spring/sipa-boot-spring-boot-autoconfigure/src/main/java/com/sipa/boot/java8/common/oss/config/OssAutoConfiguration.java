package com.sipa.boot.java8.common.oss.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.common.oss.OssContext;
import com.sipa.boot.java8.common.oss.OssStrategyFactory;
import com.sipa.boot.java8.common.oss.property.OssProperties;
import com.sipa.boot.java8.common.oss.strategy.IOssStrategy;

/**
 * @author feizhihao
 * @date 2019-08-20 18:15
 */
@Configuration
@ConditionalOnClass({OssProperties.class})
@ConditionalOnProperty(name = "sipa.boot.oss.strategy")
@ComponentScan(value = {"com.sipa.boot.java8.**.common.oss.**"})
public class OssAutoConfiguration {
    private final OssProperties ossProperties;

    public OssAutoConfiguration(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    @Bean
    public IOssStrategy ossStrategy() {
        IOssStrategy ossStrategy = OssStrategyFactory.getInstance().getOssStrategy(ossProperties);
        OssContext.init(ossProperties, ossStrategy);
        return ossStrategy;
    }
}
