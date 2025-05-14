package com.sipa.boot.java8.iot.core.message.codec.base;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.reactivestreams.Publisher;

import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IMessage;
import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSession;

import reactor.core.publisher.Mono;

/**
 * 发送给设备的上下文,在设备已经在平台中建立会话后,平台下发的指令都会使用此上下文接口
 *
 * @author caszhou
 * @date 2021/10/3
 */
public interface IToDeviceMessageContext extends IMessageEncodeContext {
    /**
     * 直接发送消息给设备
     *
     * @param message
     *            消息
     * @return 是否成功
     */
    Mono<Boolean> sendToDevice(@Nonnull IEncodedMessage message);

    /**
     * 断开设备与平台的连接
     *
     * @return void
     */
    Mono<Void> disconnect();

    /**
     * @return 获取设备会话
     */
    @Nonnull
    IDeviceSession getSession();

    /**
     * 获取指定设备的会话
     *
     * @param deviceId
     *            设备ID
     * @return mono
     */
    Mono<IDeviceSession> getSession(String deviceId);

    /**
     * 使用新的消息和设备，转换为新上下文. 通常用于在网关设备协议中,调用子设备协议时.通过此方法将上下为变换为对子设备对操作上下文.
     *
     * @param anotherMessage
     *            设备消息
     * @param device
     *            设备操作接口
     * @return 上下文
     */
    @Override
    default IToDeviceMessageContext mutate(IMessage anotherMessage, IDeviceOperator device) {
        return new IToDeviceMessageContext() {
            @Override
            public Mono<Boolean> sendToDevice(@Nonnull IEncodedMessage message) {
                return IToDeviceMessageContext.this.sendToDevice(message);
            }

            @Override
            public Mono<Void> disconnect() {
                return IToDeviceMessageContext.this.disconnect();
            }

            @Nonnull
            @Override
            public IDeviceSession getSession() {
                return IToDeviceMessageContext.this.getSession();
            }

            @Override
            public Mono<IDeviceSession> getSession(String deviceId) {
                return IToDeviceMessageContext.this.getSession(deviceId);
            }

            @Override
            public Map<String, Object> getConfiguration() {
                return IToDeviceMessageContext.this.getConfiguration();
            }

            @Override
            public Optional<Object> getConfig(String key) {
                return IToDeviceMessageContext.this.getConfig(key);
            }

            @Nonnull
            @Override
            public IMessage getMessage() {
                return anotherMessage;
            }

            @Override
            public Mono<IDeviceOperator> getDevice(String deviceId) {
                return IToDeviceMessageContext.this.getDevice(deviceId);
            }

            @Nullable
            @Override
            public IDeviceOperator getDevice() {
                return device;
            }

            @Nonnull
            @Override
            public Mono<Void> reply(@Nonnull IDeviceMessage... messages) {
                return IToDeviceMessageContext.this.reply(messages);
            }

            @Nonnull
            @Override
            public Mono<Void> reply(@Nonnull Collection<? extends IDeviceMessage> messages) {
                return IToDeviceMessageContext.this.reply(messages);
            }

            @Nonnull
            @Override
            public Mono<Void> reply(@Nonnull Publisher<? extends IDeviceMessage> replyMessage) {
                return IToDeviceMessageContext.this.reply(replyMessage);
            }
        };
    }
}
