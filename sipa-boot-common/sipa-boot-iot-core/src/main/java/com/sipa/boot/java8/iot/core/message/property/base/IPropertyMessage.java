package com.sipa.boot.java8.iot.core.message.property.base;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sipa.boot.java8.iot.core.message.property.SimpleProperty;

/**
 * @author caszhou
 * @date 2021/9/23
 */
public interface IPropertyMessage {
    Map<String, Object> getProperties();

    Map<String, Long> getPropertySourceTimes();

    Map<String, String> getPropertyStates();

    long getTimestamp();

    default Optional<Long> getPropertySourceTime(String property) {
        Map<String, Long> sourceTime = getPropertySourceTimes();
        if (MapUtils.isEmpty(sourceTime)) {
            return Optional.empty();
        }
        return Optional.of(sourceTime.get(property));
    }

    default Optional<String> getPropertyState(String property) {
        Map<String, String> states = getPropertyStates();
        if (MapUtils.isEmpty(states)) {
            return Optional.empty();
        }
        return Optional.of(states.get(property));
    }

    default Optional<Object> getProperty(String property) {
        return Optional.ofNullable(getProperties()).map(props -> props.get(property));
    }

    default Optional<IProperty> getCompleteProperty(String property) {
        return this.getProperty(property).map(value -> {
            long ts = getPropertySourceTime(property).orElse(getTimestamp());
            String state = getPropertyState(property).orElse(null);
            return new SimpleProperty(property, value, ts, state);
        });
    }

    @JsonIgnore
    @JSONField(serialize = false)
    default List<IProperty> getCompleteProperties() {
        Map<String, Object> properties = getProperties();
        if (MapUtils.isEmpty(properties)) {
            return Collections.emptyList();
        }
        return properties.entrySet().stream().map(prop -> {
            long ts = getPropertySourceTime(prop.getKey()).orElse(getTimestamp());
            String state = getPropertyState(prop.getKey()).orElse(null);
            return new SimpleProperty(prop.getKey(), prop.getValue(), ts, state);
        }).collect(Collectors.toList());
    }
}
