package com.sipa.boot.java8.iot.core.server.session.base;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;

import javax.annotation.Nullable;

import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.message.codec.base.IEncodedMessage;
import com.sipa.boot.java8.iot.core.message.codec.base.ITransport;

import reactor.core.publisher.Mono;

/**
 * 设备会话,通常对应一个设备连接
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceSession {
    /**
     * @return 会话ID
     */
    String getId();

    /**
     * @return 设备ID
     */
    String getDeviceId();

    /**
     * 获取设备操作对象,在类似TCP首次请求的场景下,返回值可能为<code>null</code>. 可以通过判断此返回值是否为<code>null</code>,来处理首次连接的情况。
     *
     * @return void
     */
    @Nullable
    IDeviceOperator getOperator();

    /**
     * @return 最近心跳时间
     */
    long lastPingTime();

    /**
     * @return 创建时间
     */
    long connectTime();

    /**
     * 发送消息给会话
     *
     * @param encodedMessage
     *            消息
     * @return 是否成功
     */
    Mono<Boolean> send(IEncodedMessage encodedMessage);

    /**
     * 传输协议,比如MQTT,TCP等
     *
     * @return void
     */
    ITransport getTransport();

    /**
     * 关闭session
     */
    void close();

    /**
     * 心跳
     */
    void ping();

    /**
     * @return 会话是否存活
     */
    boolean isAlive();

    /**
     * 设置close回调
     *
     * @param call
     *            回调
     */
    void onClose(Runnable call);

    /**
     * @return 会话连接的服务ID
     */
    default Optional<String> getServerId() {
        return Optional.empty();
    }

    /**
     * @return 客户端地址
     */
    default Optional<InetSocketAddress> getClientAddress() {
        return Optional.empty();
    }

    default void keepAlive() {
        ping();
    }

    /**
     * 设置心跳超时时间
     *
     * @param timeout
     *            心跳超时时间
     */
    default void setKeepAliveTimeout(Duration timeout) {
    }

    default Duration getKeepAliveTimeout() {
        return Duration.ZERO;
    }

    /**
     * 判断会话是否包装自指定的类型,在某些场景下,可能会对会话进行包装
     *
     * @param type
     *            类型
     * @return 是否包装自指定的类型
     */
    default boolean isWrapFrom(Class<?> type) {
        return type.isInstance(this);
    }

    /**
     * 展开为指定对会话类型,通过此方法拿到最原始对会话对象进行操作.如果类型不一致可能会抛出{@link ClassCastException}
     *
     * @param type
     *            类型
     * @param <T>
     *            类型泛型
     * @return 指定类型对会话
     */
    default <T extends IDeviceSession> T unwrap(Class<T> type) {
        return type.cast(this);
    }
}
