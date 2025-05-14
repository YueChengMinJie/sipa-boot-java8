package com.sipa.boot.java8.iot.core.message.base;

import javax.validation.constraints.NotNull;

import com.sipa.boot.java8.iot.core.enumerate.EErrorCode;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public interface IDeviceMessageReply extends IDeviceMessage {
    boolean isSuccess();

    String getCode();

    String getMessage();

    IDeviceMessageReply error(EErrorCode errorCode);

    IDeviceMessageReply error(Throwable err);

    IDeviceMessageReply deviceId(String deviceId);

    IDeviceMessageReply success();

    IDeviceMessageReply code(@NotNull String code);

    IDeviceMessageReply message(@NotNull String message);

    IDeviceMessageReply from(@NotNull IMessage message);

    IDeviceMessageReply messageId(@NotNull String messageId);

    @Override
    IDeviceMessageReply addHeader(@NotNull String header, @NotNull Object value);

    @Override
    default <T> IDeviceMessageReply addHeader(@NotNull IHeaderKey<T> header, @NotNull T value) {
        addHeader(header.getKey(), value);
        return this;
    }
}
