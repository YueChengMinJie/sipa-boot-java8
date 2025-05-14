package com.sipa.boot.java8.iot.core.protocol;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nonnull;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupports;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
@Primary
@Component
public class DefaultProtocolSupports implements IProtocolSupports, BeanPostProcessor {
    private final List<IProtocolSupports> supports = new CopyOnWriteArrayList<>();

    public void register(IProtocolSupports supports) {
        this.supports.add(supports);
    }

    @Override
    public boolean isSupport(String protocol) {
        return supports.stream().anyMatch(supports -> supports.isSupport(protocol));
    }

    @Override
    public Mono<IProtocolSupport> getProtocol(String protocol) {
        return supports.stream()
            .filter(supports -> supports.isSupport(protocol))
            .findFirst()
            .map(supports -> supports.getProtocol(protocol))
            .orElseGet(() -> Mono.error(new UnsupportedOperationException("不支持的协议:" + protocol)));
    }

    @Override
    public Flux<IProtocolSupport> getProtocols() {
        return Flux.fromIterable(supports).flatMap(IProtocolSupports::getProtocols);
    }

    @Override
    public Object postProcessAfterInitialization(@Nonnull Object o, @Nonnull String s) throws BeansException {
        if (o == this) {
            return o;
        }
        if (o instanceof IProtocolSupports) {
            register(((IProtocolSupports)o));
        }
        return o;
    }
}
