package com.sipa.boot.java8.iot.core.protocol.management;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;
import com.sipa.boot.java8.iot.core.protocol.management.base.IProtocolSupportLoader;
import com.sipa.boot.java8.iot.core.protocol.management.base.IProtocolSupportLoaderProvider;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
@Component
public class DefaultProtocolSupportLoader implements IProtocolSupportLoader, BeanPostProcessor {
    private final Map<String, IProtocolSupportLoaderProvider> providers = new ConcurrentHashMap<>();

    public void register(IProtocolSupportLoaderProvider provider) {
        this.providers.put(provider.getProvider(), provider);
    }

    @Override
    public Mono<? extends IProtocolSupport> load(ProtocolSupportDefinition definition) {
        return Mono.justOrEmpty(this.providers.get(definition.getProvider()))
            .switchIfEmpty(
                Mono.error(() -> new UnsupportedOperationException("unsupported provider:" + definition.getProvider())))
            .flatMap((provider) -> provider.load(definition));
    }

    @Override
    public Object postProcessAfterInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
        if (bean instanceof IProtocolSupportLoaderProvider) {
            register(((IProtocolSupportLoaderProvider)bean));
        }
        return bean;
    }
}
