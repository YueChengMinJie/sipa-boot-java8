package com.sipa.boot.java8.common.ms.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;

import com.sipa.boot.java8.common.ms.resolver.SmartLocaleResolver;

/**
 * @author zhouxiajie
 * @date 2019-01-22
 */
@Configuration
@ConditionalOnClass({ReloadableResourceBundleMessageSource.class, SmartLocaleResolver.class})
@ComponentScan(value = {"com.sipa.boot.java8.**.common.ms.**"})
public class MessageSourceAutoConfiguration {
    @Bean
    public MessageSource sipaBootMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:i18n/sipa_boot_messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheMillis(3600);
        return messageSource;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheMillis(3600);
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new SmartLocaleResolver();
    }
}
