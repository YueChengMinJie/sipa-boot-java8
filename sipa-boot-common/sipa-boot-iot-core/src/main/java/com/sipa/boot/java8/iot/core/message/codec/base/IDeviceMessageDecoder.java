package com.sipa.boot.java8.iot.core.message.codec.base;

import javax.annotation.Nonnull;

import org.reactivestreams.Publisher;

import com.sipa.boot.java8.iot.core.message.base.IMessage;

/**
 * 设备消息解码器，用于将收到设备上传的消息解码为可读的消息。
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceMessageDecoder {
    @Nonnull
    Publisher<? extends IMessage> decode(@Nonnull IMessageDecodeContext context);
}
