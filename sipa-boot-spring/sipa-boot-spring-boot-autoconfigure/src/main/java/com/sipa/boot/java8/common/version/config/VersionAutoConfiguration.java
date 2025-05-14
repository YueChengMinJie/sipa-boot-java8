package com.sipa.boot.java8.common.version.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.common.version.VersionRange;

/**
 * @author caszhou
 * @date 2021/11/10
 */
@Configuration
@ConditionalOnClass(VersionRange.class)
@ComponentScan(value = {"com.sipa.boot.java8.**.common.version.**"})
public class VersionAutoConfiguration {

}
