package com.sipa.boot.java8.common.mvc.config;

import java.util.List;

import javax.annotation.Nonnull;
import javax.servlet.Servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.java8.common.log.property.RequestLoggingProperties;
import com.sipa.boot.java8.common.mvc.convert.IEnumConverterFactory;
import com.sipa.boot.java8.common.mvc.customier.TomcatCustomizer;
import com.sipa.boot.java8.common.mvc.filter.base.*;
import com.sipa.boot.java8.common.mvc.filter.common.LoggingFilter;
import com.sipa.boot.java8.common.mvc.filter.header.KeepaliveTimeoutFilter;
import com.sipa.boot.java8.common.mvc.filter.order.OrderRepo;
import com.sipa.boot.java8.common.mvc.interceptor.CanaryInterceptor;
import com.sipa.boot.java8.common.mvc.processor.AliasModelAttributeMethodProcessor;
import com.sipa.boot.java8.common.mvc.property.MvcProperties;
import com.sipa.boot.java8.common.mvc.property.MvcRequestProperties;

/**
 * @author zhouxiajie
 * @date 2019-01-22
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, MvcProperties.class, MvcRequestProperties.class})
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@ComponentScan(value = {"com.sipa.boot.java8.**.common.mvc.**"})
public class MvcAutoConfiguration implements WebMvcConfigurer {
    // ******************************************
    // ***************** common *****************
    // ******************************************
    @Override
    public void addInterceptors(@Nonnull InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(new CanaryInterceptor());
    }

    @Override
    public void addFormatters(@Nonnull FormatterRegistry registry) {
        addDateTimeFormatter(registry);
        addIEnumConverterFactory(registry);
    }

    private void addIEnumConverterFactory(FormatterRegistry registry) {
        registry.addConverterFactory(new IEnumConverterFactory());
    }

    private void addDateTimeFormatter(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }

    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

    // ******************************************
    // ***************** filter *****************
    // ******************************************
    private static final String WEB_REQUEST_PROP_PREFIX = "sipa.boot.mvc.request";

    @Bean
    @ConfigurationProperties(prefix = WEB_REQUEST_PROP_PREFIX + ".logging")
    public RequestLoggingProperties requestLoggingProperties() {
        return new RequestLoggingProperties();
    }

    @Bean
    @ConditionalOnMissingBean(name = "requestIdFilter")
    public FilterRegistrationBean<OncePerRequestFilter> correlationIdFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        RequestIdFilter requestIdFilter = new RequestIdFilter();
        registration.setFilter(requestIdFilter);
        registration.setOrder(OrderRepo.BaseReqFilter.RequestId.getOrder());
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "userIdFilter")
    public FilterRegistrationBean<OncePerRequestFilter> userIdFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        UserIdFilter userIdFilter = new UserIdFilter();
        registration.setFilter(userIdFilter);
        registration.setOrder(OrderRepo.BaseReqFilter.UserId.getOrder());
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "tenantIdFilter")
    public FilterRegistrationBean<OncePerRequestFilter> tenantIdFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        TenantIdFilter tenantIdFilter = new TenantIdFilter();
        registration.setFilter(tenantIdFilter);
        registration.setOrder(OrderRepo.BaseReqFilter.TenantId.getOrder());
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "scopeFilter")
    public FilterRegistrationBean<OncePerRequestFilter> scopeFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        ScopeFilter scopeFilter = new ScopeFilter();
        registration.setFilter(scopeFilter);
        registration.setOrder(OrderRepo.BaseReqFilter.Scope.getOrder());
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "authoritiesFilter")
    public FilterRegistrationBean<OncePerRequestFilter> authoritiesFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        AuthoritiesFilter authoritiesFilter = new AuthoritiesFilter();
        registration.setFilter(authoritiesFilter);
        registration.setOrder(OrderRepo.BaseReqFilter.Authorities.getOrder());
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "deviceIdFilter")
    public FilterRegistrationBean<OncePerRequestFilter> deviceIdFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        DeviceIdFilter deviceIdFilter = new DeviceIdFilter();
        registration.setFilter(deviceIdFilter);
        registration.setOrder(OrderRepo.BaseReqFilter.DeviceId.getOrder());
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "clientIdFilter")
    public FilterRegistrationBean<OncePerRequestFilter> clientIdFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        ClientIdFilter clientIdFilter = new ClientIdFilter();
        registration.setFilter(clientIdFilter);
        registration.setOrder(OrderRepo.BaseReqFilter.ClientId.getOrder());
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "requestFromFilter")
    public FilterRegistrationBean<OncePerRequestFilter> requestFromFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        RequestFromFilter requestFromFilter = new RequestFromFilter();
        registration.setFilter(requestFromFilter);
        registration.setOrder(OrderRepo.BaseReqFilter.RequestFrom.getOrder());
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "userAgentFilter")
    public FilterRegistrationBean<OncePerRequestFilter> userAgentFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        UserAgentFilter userAgentFilter = new UserAgentFilter();
        registration.setFilter(userAgentFilter);
        registration.setOrder(OrderRepo.BaseReqFilter.UserAgent.getOrder());
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "commonLoggingFilter")
    public FilterRegistrationBean<OncePerRequestFilter> commonLoggingFilter(RequestLoggingProperties properties) {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        LoggingFilter loggingFilter = new LoggingFilter(false, properties);
        registration.setFilter(loggingFilter);
        registration.setOrder(OrderRepo.CommonReqFilter.Logging.getOrder());
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "requestIpFilter")
    public FilterRegistrationBean<OncePerRequestFilter> requestIpFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        RequestIpFilter requestIpFilter = new RequestIpFilter();
        registration.setFilter(requestIpFilter);
        registration.setOrder(OrderRepo.BaseReqFilter.RequestIp.getOrder());
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "keepaliveTimeoutFilter")
    public FilterRegistrationBean<OncePerRequestFilter> keepaliveTimeoutFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        KeepaliveTimeoutFilter keepaliveTimeoutFilter = new KeepaliveTimeoutFilter();
        registration.setFilter(keepaliveTimeoutFilter);
        registration.setOrder(OrderRepo.HeaderReqFilter.KEEP_ALIVE.getOrder());
        return registration;
    }

    // *****************************************
    // *********** tomcat customizer ***********
    // *****************************************
    @Bean
    @ConditionalOnClass(name = {"org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory"})
    @ConditionalOnMissingBean
    public TomcatCustomizer tomcatCustomizer() {
        return new TomcatCustomizer();
    }

    // *****************************************
    // *********** binder customizer ***********
    // *****************************************
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        AliasModelAttributeMethodProcessor aliasModelAttributeMethodProcessor =
            new AliasModelAttributeMethodProcessor(true);
        aliasModelAttributeMethodProcessor.setApplicationContext(applicationContext);
        resolvers.add(aliasModelAttributeMethodProcessor);
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}
