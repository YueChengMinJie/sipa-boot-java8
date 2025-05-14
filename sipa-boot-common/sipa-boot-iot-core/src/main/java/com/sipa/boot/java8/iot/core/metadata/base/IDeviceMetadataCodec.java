package com.sipa.boot.java8.iot.core.metadata.base;

import reactor.core.publisher.Mono;

/**
 * 物模型编解码器,用于将物模型与字符串进行互相转换
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceMetadataCodec {
    /**
     * @return 物模型标识
     */
    default String getId() {
        return this.getClass().getSimpleName();
    }

    /**
     * @return 物模型名称
     */
    default String getName() {
        return getId();
    }

    /**
     * 将数据解码为物模型
     *
     * @param source
     *            数据
     * @return 物模型
     */
    Mono<IDeviceMetadata> decode(String source);

    /**
     * 将物模型编码为字符串
     *
     * @param metadata
     *            物模型
     * @return 物模型字符串
     */
    Mono<String> encode(IDeviceMetadata metadata);
}
