package com.sipa.boot.java8.common.mvc.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sipa.boot.java8.common.ms.resolver.SmartLocaleResolver;
import com.sipa.boot.java8.common.mvc.property.MvcProperties;

/**
 * @author caszhou
 * @date 2022/2/9
 */
@Configuration
@ConditionalOnClass({MvcProperties.class, SmartLocaleResolver.class})
public class ValidatorAutoConfiguration implements WebMvcConfigurer {
    private final MessageSource sipaBootMessageSource;

    public ValidatorAutoConfiguration(MessageSource sipaBootMessageSource) {
        this.sipaBootMessageSource = sipaBootMessageSource;
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory();
        factoryBean.setMessageInterpolator(interpolatorFactory.getObject());
        factoryBean.setValidationMessageSource(sipaBootMessageSource);
        return factoryBean;
    }
}
