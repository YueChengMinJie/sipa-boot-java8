package com.sipa.boot.java8.common.hystrix.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.sipa.boot.java8.common.hystrix.property.HystrixProperties;

/**
 * @author zhouxiajie
 * @date 2019-02-03
 */
@EnableTurbine
@Configuration
@EnableHystrixDashboard
@ConditionalOnClass({HystrixProperties.class})
@EnableConfigurationProperties({HystrixProperties.class})
public class HystrixAutoConfiguration {
    private final HystrixProperties hystrixProperties;

    public HystrixAutoConfiguration(HystrixProperties hystrixProperties) {
        this.hystrixProperties = hystrixProperties;
    }

    @Bean
    public ServletRegistrationBean<HystrixMetricsStreamServlet> getServletRegistrationBean() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean<HystrixMetricsStreamServlet> registrationBean =
            new ServletRegistrationBean<>(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings(hystrixProperties.getUrlMappings());
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
}
