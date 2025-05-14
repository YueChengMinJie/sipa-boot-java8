package com.sipa.boot.java8.iot.core.message.base;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import com.sipa.boot.java8.iot.core.message.property.WritePropertyMessage;
import com.sipa.boot.java8.iot.core.message.property.WritePropertyMessageReply;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 修改设备属性消息发送器
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IWritePropertyMessageSender {
    IWritePropertyMessageSender custom(Consumer<WritePropertyMessage> messageConsumer);

    IWritePropertyMessageSender header(String header, Object value);

    IWritePropertyMessageSender messageId(String messageId);

    IWritePropertyMessageSender write(String property, Object value);

    Mono<IWritePropertyMessageSender> validate();

    /**
     * 发送消息
     *
     * @return 返回结果
     */
    Flux<WritePropertyMessageReply> send();

    default Mono<Void> sendAndForget() {
        return header(IHeaders.sendAndForget, true).async().send().then();
    }

    default IWritePropertyMessageSender write(Map<String, Object> properties) {
        properties.forEach(this::write);
        return this;
    }

    default IWritePropertyMessageSender accept(Consumer<IWritePropertyMessageSender> consumer) {
        consumer.accept(this);
        return this;
    }

    default IWritePropertyMessageSender timeout(Duration timeout) {
        return header(IHeaders.timeout, timeout.toMillis());
    }

    /**
     * 设置调用此功能为异步执行, 当消息发送到设备后,立即返回,而不等待设备返回结果. <code>{"success":true,"code":"REQUEST_HANDLING"}</code>
     *
     * @return this
     */
    default IWritePropertyMessageSender async() {
        return this.async(true);
    }

    /**
     * 设置是否异步
     *
     * @param async
     *            是否异步
     * @return this
     */
    default IWritePropertyMessageSender async(Boolean async) {
        return header(IHeaders.async, async);
    }

    default <T> IWritePropertyMessageSender header(IHeaderKey<T> header, T value) {
        return header(header.getKey(), value);
    }

    /**
     * 添加多个header到message中
     *
     * @param headers
     *            多个headers
     * @return this
     */
    default IWritePropertyMessageSender headers(Map<String, Object> headers) {
        Objects.requireNonNull(headers).forEach(this::header);
        return this;
    }
}
