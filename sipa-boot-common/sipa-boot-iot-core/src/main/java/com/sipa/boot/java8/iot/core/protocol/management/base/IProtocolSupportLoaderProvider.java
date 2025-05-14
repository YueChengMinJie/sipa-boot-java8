package com.sipa.boot.java8.iot.core.protocol.management.base;

import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;
import com.sipa.boot.java8.iot.core.protocol.management.ProtocolSupportDefinition;

import reactor.core.publisher.Mono;

/**
 * 协议加载服务商,用于根据配置加载协议支持
 *
 * @author caszhou
 * @date 2021/10/2
 */
public interface IProtocolSupportLoaderProvider {
    /**
     * @return 服务商标识
     */
    String getProvider();

    /**
     * 加载协议
     *
     * @param definition
     *            协议配置
     * @return ProtocolSupport
     */
    Mono<? extends IProtocolSupport> load(ProtocolSupportDefinition definition);
}
