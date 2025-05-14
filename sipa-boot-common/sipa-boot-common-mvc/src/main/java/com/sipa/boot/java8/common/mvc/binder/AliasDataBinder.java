package com.sipa.boot.java8.common.mvc.binder;

import java.lang.reflect.Field;
import java.util.Objects;

import javax.servlet.ServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import com.sipa.boot.java8.common.mvc.annotation.ValueFrom;

/**
 * @author caszhou
 * @date 2022/3/27
 */
public class AliasDataBinder extends ExtendedServletRequestDataBinder {
    public AliasDataBinder(Object target, String objectName) {
        super(target, objectName);
    }

    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        super.addBindValues(mpvs, request);
        Class<?> targetClass = Objects.requireNonNull(getTarget()).getClass();
        Class<?> targetFatherClass = targetClass.getSuperclass();

        Field[] fields = targetClass.getDeclaredFields();
        Field[] superFields = targetFatherClass.getDeclaredFields();

        addToMpvs(mpvs, fields);
        addToMpvs(mpvs, superFields);
    }

    private void addToMpvs(MutablePropertyValues mpvs, Field[] superFields) {
        for (Field superField : superFields) {
            ValueFrom valueFrom = superField.getAnnotation(ValueFrom.class);
            if (mpvs.contains(superField.getName()) || Objects.isNull(valueFrom)) {
                continue;
            }
            for (String alias : valueFrom.value()) {
                if (mpvs.contains(alias)) {
                    mpvs.add(superField.getName(), Objects.requireNonNull(mpvs.getPropertyValue(alias)).getValue());
                    break;
                }
            }
        }
    }
}
