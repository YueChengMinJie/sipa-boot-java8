package com.sipa.boot.java8.common.aop.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.sipa.boot.java8.common.aop.aspect.PrintTimeAspect;

/**
 * @author feizhihao
 * @date 2019-06-24 11:26
 */
@Configuration
@ConditionalOnClass({PrintTimeAspect.class})
@ComponentScan(value = {"com.sipa.boot.java8.**.aop.**"})
@EnableAspectJAutoProxy(exposeProxy = true)
public class AspectAutoConfiguration {

}
