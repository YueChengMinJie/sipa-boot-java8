package com.sipa.boot.java8.iot.core.message.interceptor.base;

import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessage;
import com.sipa.boot.java8.iot.core.message.interceptor.CompositeDeviceMessageSenderInterceptor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 设备消息发送拦截器,用于在消息发送端拦截处理消息. 可用于在一些自定义回复逻辑的场景
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceMessageSenderInterceptor {
    IDeviceMessageSenderInterceptor DO_NOTING = new IDeviceMessageSenderInterceptor() {

    };

    /**
     * 在消息发送前触发. 执行此方法后将使用返回值{@link IDeviceMessage}进行发送到设备.
     *
     * @param device
     *            设备操作接口
     * @param message
     *            消息对象
     * @return 新的消息
     */
    default Mono<IDeviceMessage> preSend(IDeviceOperator device, IDeviceMessage message) {
        return Mono.just(message);
    }

    /**
     * 在消息发送后触发.这里发送后并不是真正的发送，其实只是构造了整个发送的逻辑流{@link Flux}(参数 reply),
     *
     * @param device
     *            设备操作接口
     * @param message
     *            源消息
     * @param reply
     *            回复的消息
     * @param <R>
     *            回复的消息类型
     * @return 新的回复结果
     */
    default <R extends IDeviceMessage> Flux<R> afterSent(IDeviceOperator device, IDeviceMessage message,
        Flux<R> reply) {
        return reply;
    }

    default IDeviceMessageSenderInterceptor andThen(IDeviceMessageSenderInterceptor interceptor) {
        if (this == DO_NOTING) {
            return interceptor;
        }
        CompositeDeviceMessageSenderInterceptor composite = new CompositeDeviceMessageSenderInterceptor();
        composite.addInterceptor(this);
        composite.addInterceptor(interceptor);
        return composite;
    }

    /**
     * 排序序号,值小的在前,大的再后.
     *
     * @return 序号
     */
    default int getOrder() {
        return Integer.MAX_VALUE;
    }
}
