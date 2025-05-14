package com.sipa.boot.java8.data.hbase.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.data.hbase.property.HBaseProperties;

/**
 * @author zhouxiajie
 * @date 2019-07-12
 */
@Configuration
@ConditionalOnClass(HBaseProperties.class)
@ComponentScan(value = {"com.sipa.boot.java8.data.hbase.**"})
public class HBaseAutoConfiguration {

}
