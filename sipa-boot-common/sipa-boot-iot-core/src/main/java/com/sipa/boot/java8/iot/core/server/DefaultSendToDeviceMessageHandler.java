package com.sipa.boot.java8.iot.core.server;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

import org.reactivestreams.Publisher;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.connector.base.IDeviceMessageConnector;
import com.sipa.boot.java8.iot.core.constant.IDeviceState;
import com.sipa.boot.java8.iot.core.device.DeviceStateInfo;
import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.device.base.IDeviceRegistry;
import com.sipa.boot.java8.iot.core.enumerate.EDeviceConfigKey;
import com.sipa.boot.java8.iot.core.enumerate.EErrorCode;
import com.sipa.boot.java8.iot.core.exception.DeviceOperationException;
import com.sipa.boot.java8.iot.core.message.base.*;
import com.sipa.boot.java8.iot.core.message.child.ChildDeviceMessage;
import com.sipa.boot.java8.iot.core.message.child.ChildDeviceMessageReply;
import com.sipa.boot.java8.iot.core.message.codec.base.IEncodedMessage;
import com.sipa.boot.java8.iot.core.message.codec.base.IToDeviceMessageContext;
import com.sipa.boot.java8.iot.core.message.device.AcknowledgeDeviceMessage;
import com.sipa.boot.java8.iot.core.message.device.DisconnectDeviceMessage;
import com.sipa.boot.java8.iot.core.server.base.IMessageHandler;
import com.sipa.boot.java8.iot.core.server.session.ChildrenDeviceSession;
import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSession;
import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSessionManager;
import com.sipa.boot.java8.iot.core.util.DeviceMessageUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/3
 */
public class DefaultSendToDeviceMessageHandler {
    private static final Log log = LogFactory.get(DefaultSendToDeviceMessageHandler.class);

    private final String serverId;

    private final IDeviceSessionManager sessionManager;

    private final IMessageHandler handler;

    private final IDeviceRegistry registry;

    private final IDeviceMessageConnector messageConnector;

    public DefaultSendToDeviceMessageHandler(String serverId, IDeviceSessionManager sessionManager,
        IMessageHandler handler, IDeviceRegistry registry, IDeviceMessageConnector messageConnector) {
        this.serverId = serverId;
        this.sessionManager = sessionManager;
        this.handler = handler;
        this.registry = registry;
        this.messageConnector = messageConnector;
    }

    public void startup() {
        // 处理发往设备的消息
        handler.handleSendToDeviceMessage(serverId).subscribe(message -> {
            try {
                if (message instanceof IDeviceMessage) {
                    handleDeviceMessage(((IDeviceMessage)message));
                }
            } catch (Throwable e) {
                log.error("Handle send to device message error\n{}", message, e);
            }
        });

        // 处理设备状态检查
        handler.handleGetDeviceState(serverId,
            deviceId -> Flux.from(deviceId)
                .map(id -> new DeviceStateInfo(id,
                    sessionManager.sessionIsAlive(id) ? IDeviceState.online : IDeviceState.offline)));
    }

    protected void handleDeviceMessage(IDeviceMessage message) {
        String deviceId = message.getDeviceId();
        IDeviceSession session = sessionManager.getSession(deviceId);
        // 在当前服务
        if (session != null) {
            doSend(message, session);
        } else {
            // 判断子设备消息
            registry.getDevice(deviceId).flatMap(deviceOperator -> {
                // 获取上级设备
                return deviceOperator.getSelfConfig(EDeviceConfigKey.parentGatewayId).flatMap(registry::getDevice);
            }).flatMap(operator -> {
                ChildDeviceMessage children = new ChildDeviceMessage();
                children.setDeviceId(operator.getDeviceId());
                children.setMessageId(message.getMessageId());
                children.setTimestamp(message.getTimestamp());
                children.setChildDeviceId(deviceId);
                children.setChildDeviceMessage(message);
                // 没有传递header
                if (null != message.getHeaders()) {
                    Map<String, Object> newHeader = new ConcurrentHashMap<>(message.getHeaders());
                    newHeader.remove("productId");
                    newHeader.remove("deviceName");
                    children.setHeaders(newHeader);
                }
                message.addHeader(IHeaders.dispatchToParent, true);
                ChildrenDeviceSession childrenDeviceSession =
                    sessionManager.getSession(operator.getDeviceId(), deviceId);
                if (null != childrenDeviceSession) {
                    doSend(children, childrenDeviceSession);
                    return Mono.just(true);
                }
                IDeviceSession childrenSession = sessionManager.getSession(operator.getDeviceId());
                if (null != childrenSession) {
                    doSend(children, childrenSession);
                    return Mono.just(true);
                }
                // 回复离线
                return doReply(createReply(deviceId, message).error(EErrorCode.CLIENT_OFFLINE));
            }).switchIfEmpty(Mono.defer(() -> {
                log.warn("device[{}] not connected,send message fail", message.getDeviceId());
                return doReply(createReply(deviceId, message).error(EErrorCode.CLIENT_OFFLINE));
            })).subscribe();
        }
    }

    protected IDeviceMessageReply createReply(String deviceId, IDeviceMessage message) {
        IDeviceMessageReply reply;
        if (message instanceof IRepayableDeviceMessage) {
            reply = ((IRepayableDeviceMessage<?>)message).newReply();
        } else {
            reply = new AcknowledgeDeviceMessage();
        }
        reply.messageId(message.getMessageId()).deviceId(deviceId);
        return reply;
    }

    protected void doSend(IDeviceMessage message, IDeviceSession session) {
        DeviceMessageUtils.trace(message, "send.do.before");
        String deviceId = message.getDeviceId();
        IDeviceMessageReply reply = this.createReply(deviceId, message);
        AtomicBoolean alreadyReply = new AtomicBoolean(false);

        if (session.getOperator() == null) {
            log.warn("unsupported send message to [{}]", session);
            return;
        }

        IDeviceSession fSession = session.unwrap(IDeviceSession.class);
        boolean forget = message.getHeader(IHeaders.sendAndForget).orElse(false);

        Objects.requireNonNull(fSession.getOperator())
            .getProtocol()
            .flatMap(protocolSupport -> protocolSupport.getMessageCodec(fSession.getTransport()))
            .flatMapMany(codec -> codec.encode(new IToDeviceMessageContext() {
                @Override
                public Mono<Boolean> sendToDevice(@Nonnull IEncodedMessage message) {
                    return fSession.send(message);
                }

                @Override
                public Mono<Void> disconnect() {
                    return Mono.fromRunnable(() -> {
                        fSession.close();
                        sessionManager.unregister(fSession.getId());
                    });
                }

                @Nonnull
                @Override
                public IDeviceSession getSession() {
                    return fSession;
                }

                @Override
                public Mono<IDeviceSession> getSession(String deviceId) {
                    return Mono.justOrEmpty(sessionManager.getSession(deviceId));
                }

                @Nonnull
                @Override
                public IMessage getMessage() {
                    return message;
                }

                @Override
                public IDeviceOperator getDevice() {
                    return fSession.getOperator();
                }

                @Override
                public Mono<IDeviceOperator> getDevice(String deviceId) {
                    return registry.getDevice(deviceId);
                }

                @Nonnull
                @Override
                public Mono<Void> reply(@Nonnull Publisher<? extends IDeviceMessage> replyMessage) {
                    alreadyReply.set(true);
                    return Flux.from(replyMessage)
                        .flatMap(msg -> messageConnector.handleMessage(fSession.getOperator(), msg))
                        .then();
                }
            }))
            .flatMap(session::send)
            .reduce((r1, r2) -> r1 && r2)
            .flatMap(success -> {
                if (alreadyReply.get() || forget) {
                    return Mono.empty();
                }
                if (message.getHeader(IHeaders.async).orElse(false)) {
                    return doReply(reply.message(EErrorCode.REQUEST_HANDLING.getText())
                        .code(EErrorCode.REQUEST_HANDLING.name())
                        .success());
                }
                return Mono.just(true);
            })
            .switchIfEmpty(Mono.defer(() -> {
                // 协议没处理断开连接消息
                if (message instanceof DisconnectDeviceMessage) {
                    session.close();
                    sessionManager.unregister(session.getId());
                    return alreadyReply.get() ? Mono.empty() : doReply(createReply(deviceId, message).success());
                } else {
                    return alreadyReply.get() || forget ? Mono.empty()
                        : doReply(createReply(deviceId, message).error(EErrorCode.UNSUPPORTED_MESSAGE));
                }
            }))
            .onErrorResume(error -> {
                alreadyReply.set(true);
                if (!(error instanceof DeviceOperationException) || forget) {
                    log.error(error);
                }
                return forget ? Mono.empty() : this.doReply(reply.error(error));
            })
            .subscribe();
    }

    private Mono<Boolean> doReply(IDeviceMessageReply reply) {
        Mono<Boolean> then = Mono.just(true);
        if (reply instanceof ChildDeviceMessageReply) {
            IMessage message = ((ChildDeviceMessageReply)reply).getChildDeviceMessage();
            if (message instanceof IDeviceMessageReply) {
                then = doReply(((IDeviceMessageReply)message));
            }
        }
        return handler.reply(reply).as(mo -> {
            if (log.isDebugEnabled()) {
                return mo.doFinally(s -> log.debug("reply message {} ,[{}]", s, reply));
            }
            return mo;
        }).doOnError((error) -> log.error("reply message error", error)).then(then);
    }
}
