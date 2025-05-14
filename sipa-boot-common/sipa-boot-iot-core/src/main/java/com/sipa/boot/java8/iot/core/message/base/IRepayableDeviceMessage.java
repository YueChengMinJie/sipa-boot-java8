package com.sipa.boot.java8.iot.core.message.base;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public interface IRepayableDeviceMessage<R extends IDeviceMessageReply> extends IDeviceMessage {
    R newReply();
}
