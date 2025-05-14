package com.sipa.boot.java8.iot.core;

import com.sipa.boot.java8.iot.core.base.IValue;
import com.sipa.boot.java8.iot.core.bean.FastBeanCopier;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class SimpleValue implements IValue {
    private final Object nativeValue;

    public SimpleValue(Object nativeValue) {
        this.nativeValue = nativeValue;
    }

    @Override
    public Object get() {
        return nativeValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T as(Class<T> type) {
        if (nativeValue == null) {
            return null;
        }
        if (type.isInstance(nativeValue)) {
            return (T)nativeValue;
        }
        return FastBeanCopier.DEFAULT_CONVERT.convert(nativeValue, type, FastBeanCopier.EMPTY_CLASS_ARRAY);
    }
}
