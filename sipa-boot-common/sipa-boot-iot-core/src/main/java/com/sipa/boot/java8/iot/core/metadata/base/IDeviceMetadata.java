package com.sipa.boot.java8.iot.core.metadata.base;

import java.util.List;
import java.util.Optional;

/**
 * 物模型定义
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceMetadata extends IMetadata, IJsonable {
    /**
     * @return 所有属性定义
     */
    List<IPropertyMetadata> getProperties();

    /**
     * @return 所有功能定义
     */
    List<IFunctionMetadata> getFunctions();

    /**
     * @return 事件定义
     */
    List<IEventMetadata> getEvents();

    /**
     * @return 标签定义
     */
    List<IPropertyMetadata> getTags();

    default Optional<IEventMetadata> getEvent(String id) {
        return Optional.ofNullable(getEventOrNull(id));
    }

    IEventMetadata getEventOrNull(String id);

    default Optional<IPropertyMetadata> getProperty(String id) {
        return Optional.ofNullable(getPropertyOrNull(id));
    }

    IPropertyMetadata getPropertyOrNull(String id);

    default Optional<IFunctionMetadata> getFunction(String id) {
        return Optional.ofNullable(getFunctionOrNull(id));
    }

    IFunctionMetadata getFunctionOrNull(String id);

    default Optional<IPropertyMetadata> getTag(String id) {
        return Optional.ofNullable(getTagOrNull(id));
    }

    IPropertyMetadata getTagOrNull(String id);

    /**
     * 合并物模型，合并后返回新的物模型对象
     *
     * @param metadata
     *            要合并的物模型
     */
    default IDeviceMetadata merge(IDeviceMetadata metadata) {
        return merge(metadata, IMergeOption.DEFAULT_OPTIONS);
    }

    default IDeviceMetadata merge(IDeviceMetadata metadata, IMergeOption... options) {
        throw new UnsupportedOperationException("unsupported merge metadata");
    }
}
