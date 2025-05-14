package com.sipa.boot.java8.iot.core.message.interceptor.base;

import com.sipa.boot.java8.iot.core.message.codec.base.IEncodedMessage;
import com.sipa.boot.java8.iot.core.message.codec.base.IMessageEncodeContext;

import reactor.core.publisher.Mono;

/**
 * 设备消息解码拦截器,用于在对消息进行编码时进行自定义处理
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceMessageEncodeInterceptor extends IDeviceMessageCodecInterceptor {
    /**
     * 编码前执行 S
     *
     * @param context
     *            编码上下文
     */
    default void preEncode(IMessageEncodeContext context) {}

    /**
     * 编码后执行
     *
     * @param context
     *            编码上下文
     * @param message
     *            已编码的消息
     * @return 新的消息
     */
    default Mono<IEncodedMessage> postEncode(IMessageEncodeContext context, IEncodedMessage message) {
        return Mono.just(message);
    }
}
