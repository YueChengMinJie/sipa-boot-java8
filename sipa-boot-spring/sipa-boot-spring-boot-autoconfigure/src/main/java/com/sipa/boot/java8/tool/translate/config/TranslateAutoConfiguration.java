package com.sipa.boot.java8.tool.translate.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.tool.translate.property.TranslateProperties;

/**
 * @author caszhou
 * @date 2021/9/10
 */
@Configuration
@ConditionalOnClass(TranslateProperties.class)
@ComponentScan(value = {"com.sipa.boot.java8.tool.translate.**"})
public class TranslateAutoConfiguration {

}
