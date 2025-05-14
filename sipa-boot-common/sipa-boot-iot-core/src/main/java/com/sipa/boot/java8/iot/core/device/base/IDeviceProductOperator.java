package com.sipa.boot.java8.iot.core.device.base;

import com.sipa.boot.java8.iot.core.base.IConfigurable;
import com.sipa.boot.java8.iot.core.metadata.base.IDeviceMetadata;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 设备产品型号操作
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceProductOperator extends IConfigurable {
    String getId();

    /**
     * @return 设备产品物模型
     */
    Mono<IDeviceMetadata> getMetadata();

    /**
     * 更新设备型号元数据信息
     *
     * @param metadata
     *            元数据信息
     */
    Mono<Boolean> updateMetadata(String metadata);

    /**
     * @return 获取协议支持
     */
    Mono<IProtocolSupport> getProtocol();

    /**
     * @return 获取产品下的所有设备
     */
    Flux<IDeviceOperator> getDevices();
}
