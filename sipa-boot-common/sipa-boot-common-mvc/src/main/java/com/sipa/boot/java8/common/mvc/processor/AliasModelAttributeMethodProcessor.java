package com.sipa.boot.java8.common.mvc.processor;

import java.util.Objects;

import javax.servlet.ServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import com.sipa.boot.java8.common.mvc.binder.AliasDataBinder;

/**
 * @author caszhou
 * @date 2022/3/27
 */
public class AliasModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {
    private ApplicationContext applicationContext;

    public AliasModelAttributeMethodProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        AliasDataBinder aliasDataBinder = new AliasDataBinder(binder.getTarget(), binder.getObjectName());
        RequestMappingHandlerAdapter requestMappingHandlerAdapter =
            applicationContext.getBean(RequestMappingHandlerAdapter.class);
        Objects.requireNonNull(requestMappingHandlerAdapter.getWebBindingInitializer()).initBinder(aliasDataBinder);
        aliasDataBinder.bind(Objects.requireNonNull(request.getNativeRequest(ServletRequest.class)));
    }
}
