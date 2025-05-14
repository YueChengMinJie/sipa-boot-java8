package com.sipa.boot.java8.iot.core.device.base;

import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.sipa.boot.java8.iot.core.message.base.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 消息发送器,用于发送消息给设备.
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceMessageSender {
    /**
     * 发送一个支持回复的消息.
     * <p>
     * ⚠️: 请勿自己实现消息对象,而应该使用框架定义的3种消息. ⚠️: 如果是异步消息,将直接返回<code>{"success":true,"code":"REQUEST_HANDLING"}</code>
     */
    <R extends IDeviceMessageReply> Flux<R> send(Publisher<IRepayableDeviceMessage<R>> message);

    /**
     * 发送消息并自定义返回结果转换器
     */
    <R extends IDeviceMessage> Flux<R> send(Publisher<? extends IDeviceMessage> message,
        Function<Object, R> replyMapping);

    /**
     * 发送消息并获取返回
     */
    <R extends IDeviceMessage> Flux<R> send(IDeviceMessage message);

    /**
     * 发送消息后返回结果,不等待回复
     */
    default Mono<Void> sendAndForget(IDeviceMessage message) {
        return this.send(message.addHeader(IHeaders.async, true).addHeader(IHeaders.sendAndForget, true)).then();
    }

    /**
     * 发送消息更便捷的API
     */
    IFunctionInvokeMessageSender invokeFunction(String function);

    /**
     * 发送消息更便捷的API
     */
    IReadPropertyMessageSender readProperty(String... property);

    /**
     * 发送消息更便捷的API
     */
    IWritePropertyMessageSender writeProperty();
}
