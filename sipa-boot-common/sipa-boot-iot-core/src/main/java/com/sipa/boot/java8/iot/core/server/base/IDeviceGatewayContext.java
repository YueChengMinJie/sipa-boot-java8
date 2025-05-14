package com.sipa.boot.java8.iot.core.server.base;

import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.device.base.IDeviceProductOperator;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessage;

import reactor.core.publisher.Mono;

/**
 * 设备网关上下文,通过上下文可进行设备相关操作
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceGatewayContext {
    /**
     * 根据ID获取设备操作接口
     *
     * @param deviceId
     *            设备ID
     * @return 设备操作接口
     */
    Mono<IDeviceOperator> getDevice(String deviceId);

    /**
     * 根据产品ID获取产品操作接口
     *
     * @param productId
     *            产品ID
     * @return 产品操作接口
     */
    Mono<IDeviceProductOperator> getProduct(String productId);

    /**
     * 发送设备消息到设备网关,由平台统一处理这个消息.
     *
     * @param message
     *            设备消息
     * @return void
     */
    Mono<Void> onMessage(IDeviceMessage message);
}
