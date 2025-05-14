package com.sipa.boot.java8.common.mail.provider;

import com.sipa.boot.java8.common.mail.property.MailProperties;

/**
 * @author zhouxiajie
 * @date 2020/12/14
 */
public interface IMailConfigProvider {
    MailProperties getProperties(String tenantId);
}
