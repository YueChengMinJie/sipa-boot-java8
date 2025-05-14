package com.sipa.boot.java8.common.feign.config;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sipa.boot.java8.common.common.feign.convert.UniversalReversedEnumConverter;
import com.sipa.boot.java8.common.common.feign.interceptor.FeignBasicAuthRequestInterceptor;

import feign.Contract;
import feign.Logger;
import feign.RequestInterceptor;

/**
 * @author zhouxiajie
 * @date 2019-02-03
 */
@Configuration
@EnableFeignClients
@ConditionalOnClass({Logger.class, RequestInterceptor.class, UniversalReversedEnumConverter.class,
    FeignBasicAuthRequestInterceptor.class})
public class FeignAutoConfiguration {
    private final List<AnnotatedParameterProcessor> parameterProcessors;

    public FeignAutoConfiguration(List<AnnotatedParameterProcessor> parameterProcessors) {
        this.parameterProcessors = parameterProcessors;
    }

    @Bean
    public Contract feignContract(FormattingConversionService feignConversionService) {
        feignConversionService.addConverter(new UniversalReversedEnumConverter());
        return new SpringMvcContract(this.parameterProcessors, feignConversionService);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        String name = headerNames.nextElement();
                        String values = request.getHeader(name);

                        // 因为body不同，所以不复制
                        if ("content-length".equalsIgnoreCase(name)) {
                            continue;
                        }

                        requestTemplate.header(name, values);
                    }
                }
            }
        };
    }

    @Bean
    public FeignBasicAuthRequestInterceptor feignBasicAuthRequestInterceptor() {
        return new FeignBasicAuthRequestInterceptor();
    }
}
