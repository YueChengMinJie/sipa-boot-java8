package com.sipa.boot.java8.iot.core.message.function;

import java.io.Serializable;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class FunctionParameter implements Serializable {
    private static final long serialVersionUID = -6849794470754667710L;

    private String name;

    private Object value;

    public FunctionParameter() {}

    public FunctionParameter(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name + "(" + value + ")";
    }
}
