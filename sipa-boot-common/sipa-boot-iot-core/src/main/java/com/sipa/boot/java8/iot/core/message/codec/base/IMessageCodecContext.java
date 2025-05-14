package com.sipa.boot.java8.iot.core.message.codec.base;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;

import reactor.core.publisher.Mono;

/**
 * 消息编解码上下文
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IMessageCodecContext {
    /**
     * 获取当前上下文中到设备操作接口, 在tcp,http等场景下,此接口可能返回{@code null}
     */
    @Nullable
    IDeviceOperator getDevice();

    /**
     * 同{@link IMessageCodecContext#getDevice()},只是返回结果是Mono,不会为null.
     */
    default Mono<IDeviceOperator> getDeviceAsync() {
        return Mono.justOrEmpty(getDevice());
    }

    /**
     * 获取指定设备的操作接口. 如果设备不存在,则为{@link Mono#empty()},可以通过{@link Mono#switchIfEmpty(Mono)}进行处理.
     */
    default Mono<IDeviceOperator> getDevice(String deviceId) {
        return Mono.empty();
    }

    /**
     * 预留功能,获取配置信息
     *
     * @return 配置信息
     */
    default Map<String, Object> getConfiguration() {
        return Collections.emptyMap();
    }

    /**
     * 预留功能,获取配置信息
     *
     * @param key
     *            KEY
     * @return 配置信息
     */
    default Optional<Object> getConfig(String key) {
        return Optional.ofNullable(getConfiguration()).map(conf -> conf.get(key));
    }
}
