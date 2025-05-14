package com.sipa.boot.java8.iot.core.message.base;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import com.sipa.boot.java8.iot.core.message.property.ReadPropertyMessage;
import com.sipa.boot.java8.iot.core.message.property.ReadPropertyMessageReply;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IReadPropertyMessageSender {
    IReadPropertyMessageSender custom(Consumer<ReadPropertyMessage> messageConsumer);

    IReadPropertyMessageSender header(String header, Object value);

    IReadPropertyMessageSender messageId(String messageId);

    /**
     * 发送消息
     *
     * @return 返回结果
     */
    Flux<ReadPropertyMessageReply> send();

    default Mono<Void> sendAndForget() {
        return header(IHeaders.sendAndForget, true).send().then();
    }

    IReadPropertyMessageSender read(Collection<String> property);

    default IReadPropertyMessageSender read(String... property) {
        return read(Arrays.asList(property));
    }

    default IReadPropertyMessageSender accept(Consumer<IReadPropertyMessageSender> consumer) {
        consumer.accept(this);
        return this;
    }

    default IReadPropertyMessageSender timeout(Duration timeout) {
        return header(IHeaders.timeout, timeout.toMillis());
    }

    /**
     * 设置调用此功能为异步执行, 当消息发送到设备后,立即返回,而不等待设备返回结果. <code>{"success":true,"code":"REQUEST_HANDLING"}</code>
     *
     * @return this
     */
    default IReadPropertyMessageSender async() {
        return this.async(true);
    }

    /**
     * 设置是否异步
     *
     * @param async
     *            是否异步
     * @return this
     */
    default IReadPropertyMessageSender async(Boolean async) {
        return header(IHeaders.async, async);
    }

    default <T> IReadPropertyMessageSender header(IHeaderKey<T> header, T value) {
        return header(header.getKey(), value);
    }

    /**
     * 添加多个header到message中
     *
     * @param headers
     *            多个headers
     * @return this
     */
    default IReadPropertyMessageSender headers(Map<String, Object> headers) {
        Objects.requireNonNull(headers).forEach(this::header);
        return this;
    }
}
