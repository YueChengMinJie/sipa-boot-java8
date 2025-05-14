package com.sipa.boot.java8.common.exception.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.sipa.boot.java8.common.common.exception.CustomizedErrorAttributes;
import com.sipa.boot.java8.common.common.exception.CustomizedErrorController;
import com.sipa.boot.java8.common.common.exception.DefaultWebExceptionHandling;
import com.sipa.boot.java8.common.common.exception.advice.jackson.JacksonExceptionAdviceTrait;
import com.sipa.boot.java8.common.common.exception.advice.security.SecurityExceptionAdviceTrait;
import com.sipa.boot.java8.common.common.exception.property.ExceptionHandlingProperties;

/**
 * @author songjianming
 * @date 2021/11/2
 */
@Configuration
@ConditionalOnClass({ExceptionHandlingProperties.class})
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@ComponentScan(value = {"com.sipa.boot.java8.**.common.exception.**"})
public class ExceptionAutoConfiguration {
    private ServerProperties serverProperties;

    private List<ErrorViewResolver> errorViewResolvers;

    @Autowired(required = false)
    public void setServerProperties(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Autowired(required = false)
    public void setErrorViewResolvers(List<ErrorViewResolver> errorViewResolvers) {
        this.errorViewResolvers = errorViewResolvers;
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "sipa.boot.exception")
    public ExceptionHandlingProperties exceptionHandlingProperties() {
        return new ExceptionHandlingProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultWebExceptionHandling defaultWebExceptionHandling() {
        return new DefaultWebExceptionHandling();
    }

    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
    public DefaultErrorAttributes errorAttributes() {
        return new CustomizedErrorAttributes();
    }

    @Bean
    @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
    public CustomizedErrorController basicErrorController(ErrorAttributes errorAttributes) {
        return new CustomizedErrorController(errorAttributes,
            this.serverProperties == null ? new ErrorProperties() : this.serverProperties.getError(),
            this.errorViewResolvers);
    }

    @Bean
    @ConditionalOnClass(name = "com.fasterxml.jackson.databind.ObjectMapper")
    @ConditionalOnMissingBean
    public static JacksonExceptionAdviceTrait
        jacksonExceptionAdviceTrait(DefaultWebExceptionHandling defaultWebExceptionHandling) {
        return defaultWebExceptionHandling::handle;
    }

    @Bean
    @ConditionalOnClass(name = {"org.springframework.security.core.Authentication",
        "org.springframework.security.access.AccessDeniedException",
        "org.springframework.security.web.csrf.CsrfException"})
    @ConditionalOnMissingBean
    public static SecurityExceptionAdviceTrait
        securityExceptionAdviceTrait(DefaultWebExceptionHandling defaultWebExceptionHandling) {
        return defaultWebExceptionHandling::handle;
    }
}
