package com.sipa.boot.java8.iot.core.device;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.device.base.IDeviceOperationBroker;
import com.sipa.boot.java8.iot.core.device.base.IReplyFailureHandler;
import com.sipa.boot.java8.iot.core.enumerate.EErrorCode;
import com.sipa.boot.java8.iot.core.exception.DeviceOperationException;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessageReply;
import com.sipa.boot.java8.iot.core.message.base.IHeaders;
import com.sipa.boot.java8.iot.core.message.base.IMessage;
import com.sipa.boot.java8.iot.core.message.broadcast.base.IBroadcastMessage;
import com.sipa.boot.java8.iot.core.server.base.IMessageHandler;

import reactor.core.Disposable;
import reactor.core.publisher.*;

/**
 * @author caszhou
 * @date 2021/10/3
 */
@Component
public class DefaultDeviceOperationBroker implements IDeviceOperationBroker, IMessageHandler {
    private static final Log log = LogFactory.get(DefaultDeviceOperationBroker.class);

    private final Map<String, FluxProcessor<IDeviceMessageReply, IDeviceMessageReply>> replyProcessor =
        new ConcurrentHashMap<>();

    private final Map<String, AtomicInteger> partCache = new ConcurrentHashMap<>();

    private final IReplyFailureHandler replyFailureHandler =
        (error, message) -> DefaultDeviceOperationBroker.log.warn("unhandled reply message\n{}", message, error);

    private final Map<String, Function<Publisher<String>, Flux<DeviceStateInfo>>> stateHandler =
        new ConcurrentHashMap<>();

    private final FluxProcessor<IMessage, IMessage> messageEmitterProcessor;

    private final FluxSink<IMessage> sink;

    public DefaultDeviceOperationBroker() {
        this(EmitterProcessor.create(false));
    }

    public DefaultDeviceOperationBroker(FluxProcessor<IMessage, IMessage> processor) {
        this.messageEmitterProcessor = processor;
        this.sink = processor.sink(FluxSink.OverflowStrategy.BUFFER);
    }

    @Override
    public Flux<IMessage> handleSendToDeviceMessage(String serverId) {
        return messageEmitterProcessor.map(Function.identity());
    }

    @Override
    public Disposable handleGetDeviceState(String serverId,
        Function<Publisher<String>, Flux<DeviceStateInfo>> stateMapper) {
        stateHandler.put(serverId, stateMapper);
        return () -> stateHandler.remove(serverId);
    }

    @Override
    public Flux<DeviceStateInfo> getDeviceState(String serviceId, Collection<String> deviceIdList) {
        return Mono.justOrEmpty(stateHandler.get(serviceId))
            .flatMapMany(fun -> fun.apply(Flux.fromIterable(deviceIdList)));
    }

    @Override
    public Mono<Boolean> reply(IDeviceMessageReply message) {
        return Mono.defer(() -> {
            String messageId = message.getMessageId();
            if (StringUtils.isEmpty(messageId)) {
                log.warn("reply message messageId is empty\n{}", message);
                return Mono.just(false);
            }

            String partMsgId = message.getHeader(IHeaders.fragmentBodyMessageId).orElse(null);
            if (partMsgId != null) {
                FluxProcessor<IDeviceMessageReply, IDeviceMessageReply> processor =
                    replyProcessor.getOrDefault(partMsgId, replyProcessor.get(messageId));

                if (processor == null || processor.isDisposed()) {
                    replyFailureHandler.handle(new NullPointerException("no reply handler"), message);
                    replyProcessor.remove(partMsgId);
                    return Mono.just(false);
                }
                int partTotal = message.getHeader(IHeaders.fragmentNumber).orElse(1);
                AtomicInteger counter = partCache.computeIfAbsent(partMsgId, ignore -> new AtomicInteger(partTotal));

                processor.onNext(message);
                if (counter.decrementAndGet() <= 0) {
                    processor.onComplete();
                    replyProcessor.remove(partMsgId);
                }
                return Mono.just(true);
            }

            FluxProcessor<IDeviceMessageReply, IDeviceMessageReply> processor = replyProcessor.get(messageId);
            if (processor != null && !processor.isDisposed()) {
                processor.onNext(message);
                processor.onComplete();
            } else {
                replyProcessor.remove(messageId);
                replyFailureHandler.handle(new NullPointerException("no reply handler"), message);
                return Mono.just(false);
            }

            return Mono.just(true);
        }).doOnError(err -> replyFailureHandler.handle(err, message));
    }

    @Override
    public Flux<IDeviceMessageReply> handleReply(String deviceId, String messageId, Duration timeout) {
        return replyProcessor.computeIfAbsent(messageId, ignore -> UnicastProcessor.create())
            .timeout(timeout, Mono.error(() -> new DeviceOperationException(EErrorCode.TIME_OUT)))
            .doFinally(signal -> replyProcessor.remove(messageId));
    }

    @Override
    public Mono<Integer> send(String serverId, Publisher<? extends IMessage> message) {
        if (!messageEmitterProcessor.hasDownstreams()) {
            return Mono.just(0);
        }

        return Flux.from(message)
            .doOnNext(sink::next)
            .then(Mono.just(Long.valueOf(messageEmitterProcessor.downstreamCount()).intValue()));
    }

    @Override
    public Mono<Integer> send(Publisher<? extends IBroadcastMessage> message) {
        return Mono.just(0);
    }
}
