package com.sipa.boot.java8.iot.core.server.session.base;

import java.util.Optional;

import com.sipa.boot.java8.iot.core.device.base.IDeviceRegistry;
import com.sipa.boot.java8.iot.core.server.session.DeviceSessionProviders;

import reactor.core.publisher.Mono;

/**
 * 设备会话提供者
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceSessionProvider {
    /**
     * @return 提供者ID
     */
    String getId();

    /**
     * 反序列化会话
     *
     * @param sessionData
     *            会话数据
     * @param registry
     *            注册中心
     * @return 会话
     */
    Mono<IPersistentSession> deserialize(byte[] sessionData, IDeviceRegistry registry);

    /**
     * 序列化会话
     *
     * @param session
     *            会话
     * @param registry
     *            注册中心
     * @return 序列化后的数据
     */
    Mono<byte[]> serialize(IPersistentSession session, IDeviceRegistry registry);

    /**
     * 根据id获取Provider
     *
     * @param id
     *            ID
     * @return Provider
     */
    static Optional<IDeviceSessionProvider> lookup(String id) {
        return DeviceSessionProviders.lookup(id);
    }
}
