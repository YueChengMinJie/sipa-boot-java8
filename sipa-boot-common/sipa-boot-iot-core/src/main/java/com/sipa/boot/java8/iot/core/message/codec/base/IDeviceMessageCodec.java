package com.sipa.boot.java8.iot.core.message.codec.base;

import reactor.core.publisher.Mono;

/**
 * 设备消息转换器,用于对不同协议的消息进行转换
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceMessageCodec extends IDeviceMessageEncoder, IDeviceMessageDecoder {
    /**
     * @return 返回支持的传输协议
     */
    ITransport getSupportTransport();

    /**
     * 获取协议描述
     *
     * @return 协议描述
     */
    default Mono<? extends IMessageCodecDescription> getDescription() {
        return Mono.empty();
    }
}
