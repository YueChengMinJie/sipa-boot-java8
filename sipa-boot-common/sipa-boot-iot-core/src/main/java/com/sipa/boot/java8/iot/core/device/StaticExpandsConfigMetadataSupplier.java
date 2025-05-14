package com.sipa.boot.java8.iot.core.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sipa.boot.java8.iot.core.device.base.IExpandsConfigMetadataSupplier;
import com.sipa.boot.java8.iot.core.enumerate.EDeviceMetadataType;
import com.sipa.boot.java8.iot.core.metadata.base.IConfigMetadata;

import reactor.core.publisher.Flux;

/**
 * @author caszhou
 * @date 2021/10/5
 */
public class StaticExpandsConfigMetadataSupplier implements IExpandsConfigMetadataSupplier {
    private final Map<String, List<IConfigMetadata>> metadata = new ConcurrentHashMap<>();

    private List<IConfigMetadata> getOrCreateConfigs(String id) {
        return metadata.computeIfAbsent(id, ignore -> new ArrayList<>());
    }

    /**
     * 添加配置,所有物模型都有此配置
     *
     * @param configMetadata
     *            配置信息
     * @return this
     */
    public StaticExpandsConfigMetadataSupplier addConfigMetadata(IConfigMetadata configMetadata) {
        getOrCreateConfigs("any:any").add(configMetadata);
        return this;
    }

    /**
     * 添加通用配置,根据类型来指定配置
     *
     * @param typeId
     *            类型ID
     * @param configMetadata
     *            配置
     * @return this
     */
    public StaticExpandsConfigMetadataSupplier addConfigMetadata(String typeId, IConfigMetadata configMetadata) {
        getOrCreateConfigs(String.join(":", "any", typeId)).add(configMetadata);
        return this;
    }

    /**
     * 添加通用配置,指定都物模型都使用指定都配置
     *
     * @param metadataType
     *            物模型类型
     * @param configMetadata
     *            配置
     * @return this
     */
    public StaticExpandsConfigMetadataSupplier addConfigMetadata(EDeviceMetadataType metadataType,
        IConfigMetadata configMetadata) {
        return addConfigMetadata(metadataType, "any", configMetadata);
    }

    /**
     * 添加通用配置,指定都物模型以及数据类型使用指定的配置
     *
     * @param metadataType
     *            物模型类型
     * @param configMetadata
     *            配置
     * @return this
     */
    public StaticExpandsConfigMetadataSupplier addConfigMetadata(EDeviceMetadataType metadataType, String typeId,
        IConfigMetadata configMetadata) {
        getOrCreateConfigs(String.join(":", metadataType.name(), typeId)).add(configMetadata);
        return this;
    }

    @Override
    public Flux<IConfigMetadata> getConfigMetadata(EDeviceMetadataType metadataType, String metadataId,
        String dataTypeId) {
        return Flux.merge(Flux.fromIterable(metadata.getOrDefault("any:any", Collections.emptyList())),
            Flux.fromIterable(metadata.getOrDefault(String.join(":", "any", dataTypeId), Collections.emptyList())),
            Flux.fromIterable(
                metadata.getOrDefault(String.join(":", metadataType.name(), "any"), Collections.emptyList())),
            Flux.fromIterable(
                metadata.getOrDefault(String.join(":", metadataType.name(), dataTypeId), Collections.emptyList())));
    }
}
