package com.sipa.boot.java8.iot.core.spi.base;

import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

/**
 * 设备协议支持提供商
 *
 * @author caszhou
 * @date 2021/10/2
 */
public interface IProtocolSupportProvider extends Disposable {
    /**
     * 创建协议支持
     */
    Mono<? extends IProtocolSupport> create(IServiceContext context);

    /**
     * 已弃用，请实现dispose
     */
    @Deprecated
    default void close() {
    }

    /**
     * 清除
     */
    @Override
    default void dispose() {
        close();
    }
}
