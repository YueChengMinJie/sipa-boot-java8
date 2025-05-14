package com.sipa.boot.java8.iot.core.message.interceptor.base;

import com.sipa.boot.java8.iot.core.message.base.IMessage;
import com.sipa.boot.java8.iot.core.message.codec.base.IMessageDecodeContext;

import reactor.core.publisher.Mono;

/**
 * 设备消息解码拦截器
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceMessageDecodeInterceptor extends IDeviceMessageCodecInterceptor {
    /**
     * 解码前执行
     *
     * @param context
     *            上下文
     */
    default void preDecode(IMessageDecodeContext context) {}

    /**
     * 解码后执行
     *
     * @param context
     *            消息上下文
     * @param deviceMessage
     *            解码后的设备消息
     * @return 新的设备消息
     */
    default <T extends IMessage, R extends T> Mono<T> postDecode(IMessageDecodeContext context, R deviceMessage) {
        return Mono.empty();
    }
}
