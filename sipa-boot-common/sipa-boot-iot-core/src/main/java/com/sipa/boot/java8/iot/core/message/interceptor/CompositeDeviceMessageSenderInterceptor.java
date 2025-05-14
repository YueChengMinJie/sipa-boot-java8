package com.sipa.boot.java8.iot.core.message.interceptor;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessage;
import com.sipa.boot.java8.iot.core.message.interceptor.base.IDeviceMessageSenderInterceptor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class CompositeDeviceMessageSenderInterceptor implements IDeviceMessageSenderInterceptor {
    private final List<IDeviceMessageSenderInterceptor> interceptors = new CopyOnWriteArrayList<>();

    public void addInterceptor(IDeviceMessageSenderInterceptor interceptor) {
        interceptors.add(interceptor);
        interceptors.sort(Comparator.comparingInt(IDeviceMessageSenderInterceptor::getOrder));
    }

    @Override
    public Mono<IDeviceMessage> preSend(IDeviceOperator device, IDeviceMessage message) {
        Mono<IDeviceMessage> mono = Mono.just(message);
        for (IDeviceMessageSenderInterceptor interceptor : interceptors) {
            mono = mono.flatMap(msg -> interceptor.preSend(device, msg));
        }
        return mono;
    }

    @Override
    public <R extends IDeviceMessage> Flux<R> afterSent(IDeviceOperator device, IDeviceMessage message, Flux<R> reply) {
        Flux<R> flux = reply;
        for (IDeviceMessageSenderInterceptor interceptor : interceptors) {
            flux = interceptor.afterSent(device, message, flux);
        }
        return flux;
    }
}
