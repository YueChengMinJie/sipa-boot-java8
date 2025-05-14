package com.sipa.boot.java8.iot.core.exception;

import com.sipa.boot.java8.iot.core.i18n.LocaleUtils;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class I18nSupportException extends RuntimeException {
    private String i18nCode;

    private Object[] args;

    protected I18nSupportException() {
    }

    public I18nSupportException(String code, Object... args) {
        super(code);
        this.i18nCode = code;
        this.args = args;
    }

    public I18nSupportException(String code, Throwable cause, Object... args) {
        super(code, cause);
        this.args = args;
        this.i18nCode = code;
    }

    @Override
    public String getMessage() {
        return super.getMessage() != null ? super.getMessage() : getLocalizedMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return LocaleUtils.resolveMessage(i18nCode, args);
    }

    public String getI18nCode() {
        return i18nCode;
    }

    protected void setI18nCode(String i18nCode) {
        this.i18nCode = i18nCode;
    }

    public Object[] getArgs() {
        return args;
    }

    protected void setArgs(Object[] args) {
        this.args = args;
    }
}
