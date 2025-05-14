package com.sipa.boot.java8.iot.core.device.base;

import com.sipa.boot.java8.iot.core.device.StaticExpandsConfigMetadataSupplier;
import com.sipa.boot.java8.iot.core.enumerate.EDeviceMetadataType;
import com.sipa.boot.java8.iot.core.metadata.base.IConfigMetadata;

import reactor.core.publisher.Flux;

/**
 * @author caszhou
 * @date 2021/10/5
 */
public interface IExpandsConfigMetadataSupplier {
    static StaticExpandsConfigMetadataSupplier create() {
        return new StaticExpandsConfigMetadataSupplier();
    }

    /**
     * 获取物模型拓展配置信息
     *
     * @param metadataType
     *            物模型类型
     * @param metadataId
     *            物模型标识
     * @param dataTypeId
     *            数据类型ID
     * @return 配置信息
     */
    Flux<IConfigMetadata> getConfigMetadata(EDeviceMetadataType metadataType, String metadataId, String dataTypeId);
}
