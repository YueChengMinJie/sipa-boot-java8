package com.sipa.boot.java8.common.ribbon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.netflix.loadbalancer.IRule;
import com.sipa.boot.java8.common.ribbon.rule.MetadataAwareRule;

/**
 * The Ribbon discovery filter auto configuration.
 *
 * @author Xiajie Zhou
 */
@Configuration
@EnableEurekaClient
@EnableDiscoveryClient
@ConditionalOnClass({MetadataAwareRule.class})
@AutoConfigureBefore(RibbonClientConfiguration.class)
public class RibbonAutoConfiguration {
    @Value("${eureka.instance.metadata-map.canary:}")
    private String eurekaInstanceMetadataCanary;

    @Bean
    @ConditionalOnMissingBean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnProperty(value = "ribbon.filter.metadata.enabled", havingValue = "true")
    public IRule rule() {
        return new MetadataAwareRule(eurekaInstanceMetadataCanary);
    }
}
