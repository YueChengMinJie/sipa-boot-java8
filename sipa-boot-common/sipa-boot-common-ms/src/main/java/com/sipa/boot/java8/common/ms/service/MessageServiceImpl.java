package com.sipa.boot.java8.common.ms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.services.IMessageService;

/**
 * @author zhouxiajie
 * @date 2018/7/14
 */
@Service("messageService")
public class MessageServiceImpl implements IMessageService {
    private static final Log LOGGER = LogFactory.get(SipaBootMessageServiceImpl.class);

    private final MessageSource messageSource;

    @Autowired
    public MessageServiceImpl(@Qualifier("messageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(String code) {
        return this.getMessage(code, null);
    }

    @Override
    public String getMessage(String code, String[] params) {
        return messageSource.getMessage(code, params, LocaleContextHolder.getLocale());
    }

    @Override
    public String getMessageAlwaysReturn(String code) {
        return this.getMessageAlwaysReturn(code, null);
    }

    @Override
    public String getMessageAlwaysReturn(String code, String[] param) {
        try {
            return getMessage(code, param);
        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
    }
}
