package com.sipa.boot.java8.common.sms.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.common.sms.properties.AliyunSmsProperties;
import com.sipa.boot.java8.common.sms.utils.AliyunSmsUtils;

/**
 * @author sunyukun
 * @date 2019/3/19
 */
@Configuration
@ConditionalOnClass({AliyunSmsProperties.class, AliyunSmsUtils.class})
@ComponentScan(value = {"com.sipa.boot.java8.**.common.sms.**"})
public class SmsAutoConfiguration {

}
