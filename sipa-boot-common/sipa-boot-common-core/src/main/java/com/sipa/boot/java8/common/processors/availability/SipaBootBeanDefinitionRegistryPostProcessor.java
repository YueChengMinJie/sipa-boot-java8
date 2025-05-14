package com.sipa.boot.java8.common.processors.availability;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.beans.availability.SipaBootApplicationAvailabilityBean;

/**
 * @author zhouxiajie
 * @date 2021/3/24
 */
@Component
public class SipaBootBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        String beanName = "applicationAvailability";

        BeanDefinitionBuilder beanDefinitionBuilder =
            BeanDefinitionBuilder.rootBeanDefinition(SipaBootApplicationAvailabilityBean.class);

        beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory)
        throws BeansException {
    }
}
