package com.sipa.boot.java8.iot.core.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.base.IValue;
import com.sipa.boot.java8.iot.core.config.base.IConfigKey;
import com.sipa.boot.java8.iot.core.spi.base.IServiceContext;

/**
 * @author caszhou
 * @date 2021/10/2
 */
@Component
public class DefaultServiceContext implements IServiceContext {
    private static final Log log = LogFactory.get(DefaultServiceContext.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Optional<IValue> getConfig(IConfigKey<String> key) {
        return getConfig(key.getKey());
    }

    @Override
    public Optional<IValue> getConfig(String key) {
        return Optional.ofNullable(applicationContext.getEnvironment().getProperty(key)).map(IValue::simple);
    }

    @Override
    public <T> Optional<T> getService(Class<T> service) {
        try {
            return Optional.of(applicationContext.getBean(service));
        } catch (Exception e) {
            log.error("load service [{}] error", service, e);
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<T> getService(String service) {
        try {
            return Optional.of((T)applicationContext.getBean(service));
        } catch (Exception e) {
            log.error("load service [{}] error", service, e);
            return Optional.empty();
        }
    }

    @Override
    public <T> List<T> getServices(Class<T> service) {
        try {
            return new ArrayList<>(applicationContext.getBeansOfType(service).values());
        } catch (Exception e) {
            log.error("load service [{}] error", service, e);
            return Collections.emptyList();
        }
    }

    @Override
    public <T> List<T> getServices(String service) {
        return Collections.emptyList();
    }
}
