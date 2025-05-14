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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 消息编码上下文,用于平台向设备发送指令并使用协议包进行编码时,可以从上下文中获取一些参数。
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IMessageEncodeContext extends IMessageCodecContext {
    /**
     * 获取平台下发的给设备的消息指令,根据物模型中定义对应不同的消息类型. 在使用时,需要判断对应的类型进行不同的处理
     *
     * @return 消息
     */
    @Nonnull
    IMessage getMessage();

    /**
     * 直接回复消息给平台.在类似通过http接入时,下发指令可能是一个同步操作,则可以通过此方法直接回复平台.
     *
     * @param replyMessage
     *            消息流
     * @return void
     */
    @Nonnull
    default Mono<Void> reply(@Nonnull Publisher<? extends IDeviceMessage> replyMessage) {
        return Mono.empty();
    }

    /**
     * {@link IMessageEncodeContext#reply(Publisher)}
     *
     * @param messages
     *            消息
     * @return void
     */
    @Nonnull
    default Mono<Void> reply(@Nonnull Collection<? extends IDeviceMessage> messages) {
        return reply(Flux.fromIterable(messages));
    }

    /**
     * {@link IMessageEncodeContext#reply(Publisher)}
     *
     * @param messages
     *            消息
     * @return void
     */
    @Nonnull
    default Mono<Void> reply(@Nonnull IDeviceMessage... messages) {
        return reply(Flux.fromArray(messages));
    }

    /**
     * 使用新的消息和设备，转换为新上下文
     *
     * @param anotherMessage
     *            设备消息
     * @param device
     *            设备操作接口
     * @return 上下文
     */
    default IMessageEncodeContext mutate(IMessage anotherMessage, IDeviceOperator device) {
        return new IMessageEncodeContext() {
            @Override
            public Map<String, Object> getConfiguration() {
                return IMessageEncodeContext.this.getConfiguration();
            }

            @Override
            public Optional<Object> getConfig(String key) {
                return IMessageEncodeContext.this.getConfig(key);
            }

            @Nonnull
            @Override
            public IMessage getMessage() {
                return anotherMessage;
            }

            @Override
            public Mono<IDeviceOperator> getDevice(String deviceId) {
                return IMessageEncodeContext.this.getDevice(deviceId);
            }

            @Nullable
            @Override
            public IDeviceOperator getDevice() {
                return device;
            }

            @Nonnull
            @Override
            public Mono<Void> reply(@Nonnull IDeviceMessage... messages) {
                return IMessageEncodeContext.this.reply(messages);
            }

            @Nonnull
            @Override
            public Mono<Void> reply(@Nonnull Collection<? extends IDeviceMessage> messages) {
                return IMessageEncodeContext.this.reply(messages);
            }

            @Nonnull
            @Override
            public Mono<Void> reply(@Nonnull Publisher<? extends IDeviceMessage> replyMessage) {
                return IMessageEncodeContext.this.reply(replyMessage);
            }
        };
    }
}
