package com.sipa.boot.java8.iot.core.device.base;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.sipa.boot.java8.iot.core.base.IConfigurable;
import com.sipa.boot.java8.iot.core.base.IValue;
import com.sipa.boot.java8.iot.core.base.IValues;
import com.sipa.boot.java8.iot.core.config.base.IConfigKey;
import com.sipa.boot.java8.iot.core.constant.IDeviceState;
import com.sipa.boot.java8.iot.core.device.AuthenticationResponse;
import com.sipa.boot.java8.iot.core.metadata.base.IDeviceMetadata;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceOperator extends IConfigurable {
    /**
     * @return 设备ID
     */
    String getDeviceId();

    /**
     * @return 当前设备连接所在服务器ID，如果设备未上线 {@link IDeviceState#online}，则返回<code>null</code>
     */
    Mono<String> getConnectionServerId();

    /**
     * @return 当前设备连接会话ID
     */
    Mono<String> getSessionId();

    /**
     * 获取设备地址,通常是ip地址.
     *
     * @return 地址
     */
    Mono<String> getAddress();

    /**
     * 设置设备地址
     *
     * @param address
     *            地址
     * @return Mono
     */
    Mono<Void> setAddress(String address);

    /**
     * @param state
     *            状态
     * @see IDeviceState#online
     */
    Mono<Boolean> putState(byte state);

    /**
     * @return 获取当前状态
     * @see IDeviceState
     */
    Mono<Byte> getState();

    /**
     * 检查设备的真实状态
     */
    Mono<Byte> checkState();

    /**
     * @return 上线时间
     */
    Mono<Long> getOnlineTime();

    /**
     * @return 离线时间
     */
    Mono<Long> getOfflineTime();

    /**
     * 设备上线
     *
     * @param serverId
     *            设备所在服务ID
     * @param sessionId
     *            会话ID
     */
    default Mono<Boolean> online(String serverId, String sessionId) {
        return online(serverId, sessionId, null);
    }

    Mono<Boolean> online(String serverId, String sessionId, String address);

    Mono<IValue> getSelfConfig(String key);

    Mono<IValues> getSelfConfigs(Collection<String> keys);

    default Mono<IValues> getSelfConfigs(String... keys) {
        return getSelfConfigs(Arrays.asList(keys));
    }

    default <V> Mono<V> getSelfConfig(IConfigKey<V> key) {
        return getSelfConfig(key.getKey()).map(value -> value.as(key.getType()));
    }

    default Mono<IValues> getSelfConfigs(IConfigKey<?>... keys) {
        return getSelfConfigs(Arrays.stream(keys).map(IConfigKey::getKey).collect(Collectors.toSet()));
    }

    /**
     * @return 是否在线
     */
    default Mono<Boolean> isOnline() {
        return checkState().map(state -> state.equals(IDeviceState.online)).defaultIfEmpty(false);
    }

    /**
     * 设置设备离线
     *
     * @see IDeviceState#offline
     */
    Mono<Boolean> offline();

    /**
     * 断开设备连接
     *
     * @return 断开结果
     */
    Mono<Boolean> disconnect();

    /**
     * 进行授权
     *
     * @param request
     *            授权请求
     * @return 授权结果
     */
    Mono<AuthenticationResponse> authenticate(IAuthenticationRequest request);

    /**
     * @return 获取设备的元数据
     */
    Mono<IDeviceMetadata> getMetadata();

    /**
     * @return 获取此设备使用的协议支持
     */
    Mono<IProtocolSupport> getProtocol();

    /**
     * @return 消息发送器, 用于发送消息给设备
     */
    IDeviceMessageSender messageSender();

    /**
     * 设置当前设备的独立物模型,如果没有设置,这使用产品的物模型配置
     *
     * @param metadata
     *            物模型
     */
    Mono<Boolean> updateMetadata(String metadata);

    /**
     * 重置当前设备的独立物模型
     */
    Mono<Void> resetMetadata();

    /**
     * @return 获取设备对应的产品操作接口
     */
    Mono<IDeviceProductOperator> getProduct();
}
