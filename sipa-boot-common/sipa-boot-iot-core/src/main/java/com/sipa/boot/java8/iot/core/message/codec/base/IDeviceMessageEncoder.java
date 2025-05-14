package com.sipa.boot.java8.iot.core.message.codec.base;

import javax.annotation.Nonnull;

import org.reactivestreams.Publisher;

/**
 * 设备消息编码器,用于将消息对象编码为对应消息协议的消息
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceMessageEncoder {
    @Nonnull
    Publisher<? extends IEncodedMessage> encode(@Nonnull IMessageEncodeContext context);
}
