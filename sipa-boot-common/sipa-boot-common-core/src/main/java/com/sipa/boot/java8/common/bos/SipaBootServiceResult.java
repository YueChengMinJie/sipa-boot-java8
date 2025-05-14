package com.sipa.boot.java8.common.bos;

import java.io.Serializable;

/**
 * @author zhouxiajie
 * @date 2018/11/16
 */
public class SipaBootServiceResult<T> implements Serializable {
    private static final long serialVersionUID = -6076297824524727970L;

    private String message;

    private T data;

    public SipaBootServiceResult() {}

    private SipaBootServiceResult(Builder<T> builder) {
        setMessage(builder.message);
        setData(builder.data);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    private void setData(T data) {
        this.data = data;
    }

    public static final class Builder<T> {
        private String message;

        private T data;

        private Builder() {}

        public Builder message(String val) {
            message = val;
            return this;
        }

        public Builder data(T val) {
            data = val;
            return this;
        }

        public SipaBootServiceResult<T> build() {
            return new SipaBootServiceResult<>(this);
        }
    }
}
