package com.sipa.boot.java8.iot.core.metadata.base;

import java.util.Map;
import java.util.Optional;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public interface IMetadata {
    String getId();

    String getName();

    String getDescription();

    Map<String, Object> getExpands();

    default Optional<Object> getExpand(String name) {
        return Optional.ofNullable(getExpands()).map(map -> map.get(name));
    }

    default void setExpands(Map<String, Object> expands) {}

    default void setName(String name) {
    }

    default void setDescription(String description) {
    }
}
