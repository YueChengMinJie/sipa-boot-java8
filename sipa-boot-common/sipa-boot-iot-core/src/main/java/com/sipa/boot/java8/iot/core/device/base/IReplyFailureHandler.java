package com.sipa.boot.java8.iot.core.device.base;

import com.sipa.boot.java8.iot.core.message.base.IDeviceMessageReply;

/**
 * @author caszhou
 * @date 2021/10/3
 */
public interface IReplyFailureHandler {
    void handle(Throwable err, IDeviceMessageReply message);
}
