package com.sipa.boot.java8.iot.core;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;

import com.sipa.boot.java8.iot.core.base.IValue;
import com.sipa.boot.java8.iot.core.base.IValues;
import com.sipa.boot.java8.iot.core.bean.FastBeanCopier;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class SimpleValues implements IValues {
    private final Map<String, Object> values;

    public SimpleValues(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public Map<String, Object> getAllValues() {
        return new HashMap<>(values);
    }

    @Override
    public Optional<IValue> getValue(String key) {
        return Optional.ofNullable(key).map(values::get).map(IValue::simple);
    }

    @Override
    public IValues merge(IValues source) {
        Map<String, Object> merged = new HashMap<>();
        merged.putAll(this.values);
        merged.putAll(source.getAllValues());
        return IValues.of(merged);
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public Set<String> getNonExistentKeys(Collection<String> keys) {
        return keys.stream().filter(has -> !values.containsKey(has)).collect(Collectors.toSet());
    }

    @Override
    public String getString(String key, Supplier<String> defaultValue) {
        if (MapUtils.isEmpty(values)) {
            return defaultValue.get();
        }
        Object val = values.get(key);
        if (val == null) {
            return defaultValue.get();
        }
        return String.valueOf(val);
    }

    @Override
    public Number getNumber(String key, Supplier<Number> defaultValue) {
        if (MapUtils.isEmpty(values)) {
            return defaultValue.get();
        }
        Object val = values.get(key);
        if (val == null) {
            return defaultValue.get();
        }
        if (val instanceof Number) {
            return ((Number)val);
        }
        return FastBeanCopier.DEFAULT_CONVERT.convert(val, Number.class, FastBeanCopier.EMPTY_CLASS_ARRAY);
    }
}
