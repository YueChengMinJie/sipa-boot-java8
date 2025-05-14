package com.sipa.boot.java8.iot.core.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

/**
 * todo
 *
 * @author caszhou
 * @date 2021/9/24
 */
public class UnsupportedMessageSource implements MessageSource {
    private static final UnsupportedMessageSource INSTANCE = new UnsupportedMessageSource();

    public static MessageSource instance() {
        return INSTANCE;
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return defaultMessage;
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        // todo
        return code;
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return resolvable.getDefaultMessage();
    }
}
