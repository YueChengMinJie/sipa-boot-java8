package com.sipa.boot.java8.iot.core.exception;

import java.util.*;

import javax.validation.ConstraintViolation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.iot.core.i18n.LocaleUtils;

/**
 * @author caszhou
 * @date 2021/9/24
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends I18nSupportException {
    private static final boolean PROPERTY_I18N_ENABLED = Boolean.getBoolean("sipa.boot.i18n.validation.property.enabled");

    private List<Detail> details;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String property, String message, Object... args) {
        this(message, Collections.singletonList(new Detail(property, message, null)), args);
    }

    public ValidationException(String message, List<Detail> details, Object... args) {
        super(message, 400, args);
        this.details = details;
        for (Detail detail : this.details) {
            detail.translateI18n(args);
        }
    }

    public ValidationException(Set<? extends ConstraintViolation<?>> violations) {
        ConstraintViolation<?> first = violations.iterator().next();
        if (Objects.equals(first.getMessageTemplate(), first.getMessage())) {
            // 模版和消息相同,说明是自定义的message,而不是已经通过i18n获取的.
            setI18nCode(first.getMessage());
        } else {
            setI18nCode("validation.property_validate_failed");
        }
        String property = first.getPropertyPath().toString();

        // {0} 属性 ，{1} 验证消息
        String resolveMessage = PROPERTY_I18N_ENABLED
            ? LocaleUtils.resolveMessage(first.getRootBeanClass().getName() + "." + property, property) : property;

        setArgs(new Object[] {resolveMessage, first.getMessage()});

        details = new ArrayList<>(violations.size());
        for (ConstraintViolation<?> violation : violations) {
            details.add(new Detail(violation.getPropertyPath().toString(), violation.getMessage(), null));
        }
    }

    public static class Detail {
        String property;

        String message;

        Object detail;

        public Detail(String property, String message, Object detail) {
            this.property = property;
            this.message = message;
            this.detail = detail;
        }

        public void translateI18n(Object... args) {
            if (message.contains(SipaBootCommonConstants.POINT)) {
                message = LocaleUtils.resolveMessage(message, message, args);
            }
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getDetail() {
            return detail;
        }

        public void setDetail(Object detail) {
            this.detail = detail;
        }
    }
}
