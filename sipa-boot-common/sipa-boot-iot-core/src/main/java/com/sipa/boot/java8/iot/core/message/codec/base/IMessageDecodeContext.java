package com.sipa.boot.java8.iot.core.message.codec.base;

import javax.annotation.Nonnull;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IMessageDecodeContext extends IMessageCodecContext {
    /**
     * 获取设备上报的原始消息,根据通信协议的不同,消息类型也不同, 在使用时可能需要转换为对应的消息类型
     *
     * @return 原始消息
     */
    @Nonnull
    IEncodedMessage getMessage();
}
