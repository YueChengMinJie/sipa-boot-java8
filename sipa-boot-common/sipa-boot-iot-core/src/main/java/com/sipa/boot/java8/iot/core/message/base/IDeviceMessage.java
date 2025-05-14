package com.sipa.boot.java8.iot.core.message.base;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public interface IDeviceMessage extends IMessage {
    String getDeviceId();

    @Override
    long getTimestamp();

    @Override
    default <T> IDeviceMessage addHeader(IHeaderKey<T> header, T value) {
        IMessage.super.addHeader(header, value);
        return this;
    }

    @Override
    IDeviceMessage addHeader(String header, Object value);

    @Override
    default <T> IDeviceMessage addHeaderIfAbsent(IHeaderKey<T> header, T value) {
        IMessage.super.addHeaderIfAbsent(header, value);
        return this;
    }

    @Override
    IDeviceMessage addHeaderIfAbsent(String header, Object value);
}
