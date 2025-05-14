package com.sipa.boot.java8.common.archs.threadpool.pojo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * @author sunyukun
 * @since 2019/8/6 15:02
 */
public class Tuple {
    private final Object[] values;

    public Tuple(Object... value) {
        this.values = value;
    }

    public Object get(int index) {
        if (index >= values.length) {
            return null;
        }
        return values[index];
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
