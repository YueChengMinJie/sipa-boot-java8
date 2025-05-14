package com.sipa.boot.java8.iot.core.protocol;

import java.util.ServiceLoader;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupports;
import com.sipa.boot.java8.iot.core.spi.base.IProtocolSupportProvider;
import com.sipa.boot.java8.iot.core.spi.base.IServiceContext;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
@Component
public class ServiceLoaderProtocolSupports implements IProtocolSupports {
    private static final Log log = LogFactory.get(ServiceLoaderProtocolSupports.class);

    private final StaticProtocolSupports supports;

    private final IServiceContext serviceContext;

    public ServiceLoaderProtocolSupports(IServiceContext serviceContext) {
        this.supports = new StaticProtocolSupports();;
        this.serviceContext = serviceContext;
    }

    @Override
    public boolean isSupport(String protocol) {
        return supports.isSupport(protocol);
    }

    @Override
    public Mono<IProtocolSupport> getProtocol(String protocol) {
        return supports.getProtocol(protocol);
    }

    @Override
    public Flux<IProtocolSupport> getProtocols() {
        return supports.getProtocols();
    }

    @PostConstruct
    public void init() {
        ServiceLoader<IProtocolSupportProvider> loader =
            ServiceLoader.load(IProtocolSupportProvider.class, getClassLoader());
        loader.iterator()
            .forEachRemaining(provider -> provider.create(serviceContext)
                .doOnNext(pro -> log.debug("found protocol support provider [{}]", pro))
                .subscribe(supports::register));
    }

    protected ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }
}
