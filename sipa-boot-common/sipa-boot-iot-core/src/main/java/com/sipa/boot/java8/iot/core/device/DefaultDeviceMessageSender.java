package com.sipa.boot.java8.iot.core.device;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.springframework.util.StringUtils;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.constant.IDeviceState;
import com.sipa.boot.java8.iot.core.device.base.IDeviceMessageSender;
import com.sipa.boot.java8.iot.core.device.base.IDeviceOperationBroker;
import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.device.base.IDeviceRegistry;
import com.sipa.boot.java8.iot.core.enumerate.EDeviceConfigKey;
import com.sipa.boot.java8.iot.core.enumerate.EErrorCode;
import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.exception.DeviceOperationException;
import com.sipa.boot.java8.iot.core.message.base.*;
import com.sipa.boot.java8.iot.core.message.child.ChildDeviceMessage;
import com.sipa.boot.java8.iot.core.message.child.ChildDeviceMessageReply;
import com.sipa.boot.java8.iot.core.message.interceptor.base.IDeviceMessageSenderInterceptor;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;
import com.sipa.boot.java8.iot.core.util.DeviceMessageUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultDeviceMessageSender implements IDeviceMessageSender {
    private static final Log log = LogFactory.get(DefaultDeviceMessageSender.class);

    private final IDeviceOperationBroker handler;

    private final IDeviceOperator operator;

    private final IDeviceRegistry registry;

    private static final long DEFAULT_TIMEOUT =
        TimeUnit.SECONDS.toMillis(Integer.getInteger("sipa.boot.iot.device.message.default-timeout", 10));

    private long defaultTimeout = DEFAULT_TIMEOUT;

    public long getDefaultTimeout() {
        return defaultTimeout;
    }

    public void setDefaultTimeout(long defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    private final IDeviceMessageSenderInterceptor globalInterceptor;

    public DefaultDeviceMessageSender(IDeviceOperationBroker handler, IDeviceOperator operator,
        IDeviceRegistry registry, IDeviceMessageSenderInterceptor interceptor) {
        this.handler = handler;
        this.operator = operator;
        this.registry = registry;
        this.globalInterceptor = interceptor;
    }

    @Override
    public <R extends IDeviceMessageReply> Flux<R> send(Publisher<IRepayableDeviceMessage<R>> message) {
        return send(message, this::convertReply);
    }

    protected <T extends IDeviceMessageReply> T convertReply(IMessage sent, Object reply) {
        if (reply instanceof ChildDeviceMessageReply) {
            if (!(sent instanceof ChildDeviceMessage)) {
                ChildDeviceMessageReply messageReply = ((ChildDeviceMessageReply)reply);
                if (!messageReply.isSuccess()) {
                    // 如果是可识别的错误则直接抛出异常
                    EErrorCode.of(messageReply.getCode()).map(DeviceOperationException::new).ifPresent(err -> {
                        throw err;
                    });
                }
                if (messageReply.getChildDeviceMessage() == null) {
                    EErrorCode.of(messageReply.getCode()).map(DeviceOperationException::new).ifPresent(err -> {
                        throw err;
                    });
                    throw new DeviceOperationException(EErrorCode.NO_REPLY);
                }
                return convertReply(((ChildDeviceMessageReply)reply).getChildDeviceMessage());
            }
        }
        return convertReply(reply);
    }

    protected <T extends IDeviceMessage> T convertReply(Object obj) {
        IDeviceMessage result = null;
        if (obj instanceof IDeviceMessageReply) {
            IDeviceMessageReply reply = ((IDeviceMessageReply)obj);
            if (!reply.isSuccess()) {
                // 如果是可识别的错误则直接抛出异常
                EErrorCode.of(reply.getCode()).map(code -> {
                    String msg = reply.getHeader("errorMessage").map(String::valueOf).orElse(code.getText());
                    return new DeviceOperationException(code, msg);
                }).ifPresent(err -> {
                    throw err;
                });
            }
            result = reply;
        } else if (obj instanceof IDeviceMessage) {
            result = (IDeviceMessage)obj;
        } else if (obj instanceof Map) {
            result = (IDeviceMessage)EMessageType.convertMessage(((Map)obj)).orElse(null);
        }
        if (result == null) {
            throw new DeviceOperationException(EErrorCode.SYSTEM_ERROR,
                new ClassCastException("can not cast " + obj + " to IDeviceMessageReply"));
        }
        return (T)result;
    }

    private <R extends IDeviceMessage> Flux<R> logReply(IDeviceMessage msg, Flux<R> flux) {
        if (log.isDebugEnabled()) {
            return flux
                .doOnNext(
                    r -> log.debug("receive device[{}] message[{}]: {}", operator.getDeviceId(), r.getMessageId(), r))

                .doOnComplete(() -> log.debug("complete receive device[{}] message[{}]", operator.getDeviceId(),
                    msg.getMessageId()))

                .doOnCancel(() -> log.debug("cancel receive device[{}] message[{}]", operator.getDeviceId(),
                    msg.getMessageId()));
        }
        return flux;
    }

    @Override
    public <R extends IDeviceMessage> Flux<R> send(IDeviceMessage message) {
        return send(Mono.just(message), this::convertReply);
    }

    private Mono<String> refreshAndGetConnectionServerId() {
        return Mono
            .defer(() -> operator.refreshConfig(Collections.singleton(EDeviceConfigKey.connectionServerId.getKey()))
                .then(operator.getConnectionServerId()));
    }

    private Flux<IDeviceMessage> sendToParentDevice(String parentId, IDeviceMessage message) {
        if (parentId.equals(operator.getDeviceId())) {
            return Flux.error(new DeviceOperationException(EErrorCode.CYCLIC_DEPENDENCE,
                "validation.parent_id_and_id_can_not_be_same"));
        }

        ChildDeviceMessage children = new ChildDeviceMessage();
        children.setDeviceId(parentId);
        children.setMessageId(message.getMessageId());
        children.setTimestamp(message.getTimestamp());
        children.setChildDeviceId(operator.getDeviceId());
        children.setChildDeviceMessage(message);

        if (null != message.getHeaders()) {
            children.setHeaders(new ConcurrentHashMap<>(message.getHeaders()));
        }
        message.addHeader(IHeaders.dispatchToParent, true);
        children.validate();
        return registry.getDevice(parentId)
            .switchIfEmpty(Mono.error(() -> new DeviceOperationException(EErrorCode.UNKNOWN_PARENT_DEVICE)))
            .flatMapMany(
                parent -> parent.messageSender().send(Mono.just(children), resp -> this.convertReply(message, resp)));
    }

    @Override
    public <R extends IDeviceMessage> Flux<R> send(Publisher<? extends IDeviceMessage> message,
        Function<Object, R> replyMapping) {
        return Mono.zip(
            // 当前设备连接的服务器ID
            operator.getConnectionServerId().switchIfEmpty(refreshAndGetConnectionServerId()).defaultIfEmpty(""),
            // 拦截器
            operator.getProtocol()
                .flatMap(IProtocolSupport::getSenderInterceptor)
                .defaultIfEmpty(IDeviceMessageSenderInterceptor.DO_NOTING),
            // 网关id
            operator.getSelfConfig(EDeviceConfigKey.parentGatewayId).defaultIfEmpty(""))
            .flatMapMany(serverAndInterceptor -> {
                IDeviceMessageSenderInterceptor interceptor = serverAndInterceptor.getT2().andThen(globalInterceptor);
                String server = serverAndInterceptor.getT1();
                String parentGatewayId = serverAndInterceptor.getT3();
                // 设备未连接,有上级网关设备则通过父级设备发送消息
                if (StringUtils.isEmpty(server) && StringUtils.hasText(parentGatewayId)) {
                    return Flux.from(message)
                        .flatMap(msg -> interceptor.preSend(operator, msg))
                        .flatMap(msg -> this.sendToParentDevice(parentGatewayId, msg)
                            .as(flux -> interceptor.afterSent(operator, msg, flux)))
                        .map(r -> (R)r);
                }
                return Flux.from(message).flatMap(msg -> interceptor.preSend(operator, msg)).concatMap(msg -> {
                    DeviceMessageUtils.trace(msg, "send.before");
                    if (StringUtils.isEmpty(server)) {
                        return interceptor.afterSent(operator, msg,
                            Flux.error(new DeviceOperationException(EErrorCode.CLIENT_OFFLINE)));
                    }
                    boolean forget = msg.getHeader(IHeaders.sendAndForget).orElse(false);
                    // 定义处理来自设备的回复.
                    Flux<R> replyStream = forget ? Flux.empty()
                        : handler
                            .handleReply(msg.getDeviceId(), msg.getMessageId(),
                                Duration.ofMillis(msg.getHeader(IHeaders.timeout).orElse(defaultTimeout)))
                            .map(replyMapping)
                            .onErrorResume(DeviceOperationException.class, error -> {
                                if (error.getCode() == EErrorCode.CLIENT_OFFLINE) {
                                    // 返回离线错误,重新检查状态,以矫正设备缓存的状态
                                    return operator.checkState().then(Mono.error(error));
                                }
                                return Mono.error(error);
                            })
                            .onErrorMap(TimeoutException.class,
                                timeout -> new DeviceOperationException(EErrorCode.TIME_OUT, timeout))
                            .as(flux -> this.logReply(msg, flux));

                    // 发送消息到设备连接的服务器
                    return handler.send(server, Mono.just(msg)).defaultIfEmpty(-1).flatMapMany(len -> {
                        // 设备未连接到服务器
                        if (len == 0) {
                            // 尝试发起状态检查,同步设备的真实状态
                            return operator.checkState().flatMapMany(state -> {
                                if (IDeviceState.online != state) {
                                    return interceptor.afterSent(operator, msg,
                                        Flux.error(new DeviceOperationException(EErrorCode.CLIENT_OFFLINE)));
                                }
                                // 设备在线,但是serverId对应的服务没有监听处理消息
                                // 1. 服务挂了
                                // 2. 设备缓存的serverId不对
                                // 尝试发送给父设备
                                if (StringUtils.hasText(parentGatewayId)) {
                                    log.debug("Device [{}] Cached Server [{}] Not Available,Dispatch To Parent [{}]",
                                        operator.getDeviceId(), server, parentGatewayId);

                                    return interceptor
                                        .afterSent(operator, msg, sendToParentDevice(parentGatewayId, msg))
                                        .map(r -> (R)r);
                                }
                                log.warn("Device [{}] Cached Server [{}] Not Available", operator.getDeviceId(),
                                    server);

                                return interceptor.afterSent(operator, msg,
                                    Flux.error(new DeviceOperationException(EErrorCode.SERVER_NOT_AVAILABLE)));
                            });
                        } else if (len == -1) {
                            return interceptor.afterSent(operator, msg,
                                Flux.error(new DeviceOperationException(EErrorCode.CLIENT_OFFLINE)));
                        }
                        log.debug("send device[{}] message complete", operator.getDeviceId());
                        return interceptor.afterSent(operator, msg, replyStream);
                    }).doOnNext(r -> DeviceMessageUtils.trace(r, "send.reply"));
                });
            });
    }

    @Override
    public IFunctionInvokeMessageSender invokeFunction(String function) {
        return new DefaultFunctionInvokeMessageSender(operator, function);
    }

    @Override
    public IReadPropertyMessageSender readProperty(String... property) {
        return new DefaultReadPropertyMessageSender(operator).read(property);
    }

    @Override
    public IWritePropertyMessageSender writeProperty() {
        return new DefaultWritePropertyMessageSender(operator);
    }
}
