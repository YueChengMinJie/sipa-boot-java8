package com.sipa.boot.java8.iot.core.protocol;

import java.util.Map;

import com.sipa.boot.java8.common.archs.cache.Caches;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupports;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class StaticProtocolSupports implements IProtocolSupports {
    protected Map<String, IProtocolSupport> supports = Caches.newCache();

    @Override
    public boolean isSupport(String protocol) {
        return supports.containsKey(protocol);
    }

    @Override
    public Mono<IProtocolSupport> getProtocol(String protocol) {
        IProtocolSupport support = supports.get(protocol);
        if (support == null) {
            return Mono.error(new UnsupportedOperationException("不支持的协议:" + protocol));
        }
        return Mono.just(support);
    }

    @Override
    public Flux<IProtocolSupport> getProtocols() {
        return Flux.fromIterable(supports.values());
    }

    public void register(IProtocolSupport support) {
        IProtocolSupport old = supports.put(support.getId(), support);
        if (null != old) {
            old.dispose();
        }
    }

    public void unRegister(IProtocolSupport support) {
        unRegister(support.getId());
    }

    public void unRegister(String id) {
        IProtocolSupport old = supports.remove(id);
        if (null != old) {
            old.dispose();
        }
    }
}
