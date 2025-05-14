package com.sipa.boot.java8.iot.core.protocol.management.base;

import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;
import com.sipa.boot.java8.iot.core.protocol.management.ProtocolSupportDefinition;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public interface IProtocolSupportLoader {
    Mono<? extends IProtocolSupport> load(ProtocolSupportDefinition definition);
}
