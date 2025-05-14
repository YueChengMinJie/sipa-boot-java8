package com.sipa.boot.java8.common.mail.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import com.sipa.boot.java8.common.mail.property.MailProperties;
import com.sipa.boot.java8.common.mail.util.JavaMailSenderUtils;

/**
 * @author feizhihao
 * @date 2019-06-24 11:26
 */
@Configuration
@ConditionalOnClass({MailProperties.class})
@ComponentScan(value = {"com.sipa.boot.java8.**.common.mail.**"})
public class MailAutoConfiguration {
    @Autowired
    private MailProperties mailProperties;

    @Bean
    public JavaMailSender getJavaMailSender() {
        switch (mailProperties.getFrom()) {
            case 1:
                JavaMailSender mailSender1 = JavaMailSenderUtils.initJavaMailSender(mailProperties);

                JavaMailSenderUtils.putSender(mailProperties, mailSender1);

                return mailSender1;
            case 2:
            default:
                return null;
        }
    }
}
