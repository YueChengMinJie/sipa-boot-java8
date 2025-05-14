package com.sipa.boot.java8.iot.core.message.base;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import com.sipa.boot.java8.iot.core.message.function.FunctionInvokeMessage;
import com.sipa.boot.java8.iot.core.message.function.FunctionInvokeMessageReply;
import com.sipa.boot.java8.iot.core.message.function.FunctionParameter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 调用设备功能的消息发送器
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IFunctionInvokeMessageSender {
    IFunctionInvokeMessageSender custom(Consumer<FunctionInvokeMessage> messageConsumer);

    IFunctionInvokeMessageSender header(String header, Object value);

    IFunctionInvokeMessageSender addParameter(FunctionParameter parameter);

    IFunctionInvokeMessageSender setParameter(List<FunctionParameter> parameter);

    IFunctionInvokeMessageSender messageId(String messageId);

    /**
     * 执行参数校验
     *
     * <pre>
     *     function("door-open")
     *     .validate()
     *     .flatMany(IFunctionInvokeMessageSender::send)
     *     .doOnError(IllegalParameterException.class,err->log.error(err.getMessage(),err))
     *     ...
     * </pre>
     *
     * @see Mono#doOnError(Consumer)
     */
    Mono<IFunctionInvokeMessageSender> validate();

    /**
     * 发送消息
     *
     * @return 返回结果
     */
    Flux<FunctionInvokeMessageReply> send();

    /**
     * 异步发送,并忽略返回结果
     *
     * @return void
     */
    default Mono<Void> sendAndForget() {
        return header(IHeaders.sendAndForget, true).async().send().then();
    }

    default IFunctionInvokeMessageSender accept(Consumer<IFunctionInvokeMessageSender> consumer) {
        consumer.accept(this);
        return this;
    }

    default IFunctionInvokeMessageSender addParameter(String name, Object value) {
        return addParameter(new FunctionParameter(name, value));
    }

    default IFunctionInvokeMessageSender setParameter(Map<String, Object> parameter) {
        parameter.forEach(this::addParameter);
        return this;
    }

    default IFunctionInvokeMessageSender timeout(Duration timeout) {
        return header(IHeaders.timeout, timeout.toMillis());
    }

    /**
     * 设置调用此功能为异步执行, 当消息发送到设备后,立即返回,而不等待设备返回结果. <code>{"success":true,"code":"REQUEST_HANDLING"}</code>
     *
     * @return this
     */
    default IFunctionInvokeMessageSender async() {
        return this.async(true);
    }

    /**
     * 设置是否异步
     *
     * @param async
     *            是否异步
     * @return this
     * @see IFunctionInvokeMessageSender#async(Boolean)
     */
    default IFunctionInvokeMessageSender async(Boolean async) {
        return header(IHeaders.async, async);
    }

    default <T> IFunctionInvokeMessageSender header(IHeaderKey<T> header, T value) {
        return header(header.getKey(), value);
    }

    /**
     * 添加多个header到message中
     *
     * @param headers
     *            多个headers
     * @return this
     */
    default IFunctionInvokeMessageSender headers(Map<String, Object> headers) {
        Objects.requireNonNull(headers).forEach(this::header);
        return this;
    }
}
