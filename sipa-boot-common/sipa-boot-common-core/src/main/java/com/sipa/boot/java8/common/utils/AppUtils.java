package com.sipa.boot.java8.common.utils;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文工具类 Author zhfei Date 2019/5/12 1:06 PM
 */
@Component
public class AppUtils implements ApplicationContextAware {
    @Value("${spring.application.name}")
    private String applicationName;

    private static String appName;

    private static ApplicationContext applicationContext;

    /**
     * 服务器启动，Spring容器初始化时，当加载了当前类为bean组件后， 将会调用下面方法注入ApplicationContext实例
     */
    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        AppUtils.applicationContext = arg0;
        AppUtils.appName = applicationName;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setContext(ApplicationContext arg0) {
        AppUtils.applicationContext = arg0;
    }

    public static String getAppName() {
        return appName;
    }

    /**
     * 外部调用这个getBean方法就可以手动获取到bean 用bean组件的name来获取bean
     */
    public static <T> T getBean(String beanName) {
        return (T)applicationContext.getBean(beanName);
    }

    /**
     * 外部调用这个getBean方法就可以手动获取到bean 用bean组件的name来获取bean
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }

    /**
     * 根据类从spring上下文获取bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据接口获取所有实现类bean
     */
    public static <T> Map<String, T> getBeans(Class<T> interfaceClazz) {
        return applicationContext.getBeansOfType(interfaceClazz);
    }
}
