package com.sipa.boot.java8.iot.core.connector.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.message.base.IMessage;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceMessageConnector {
    Mono<Boolean> handleMessage(@Nullable IDeviceOperator operator, @Nonnull IMessage message);
}
