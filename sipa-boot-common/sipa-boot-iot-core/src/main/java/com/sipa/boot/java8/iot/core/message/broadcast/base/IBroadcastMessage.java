package com.sipa.boot.java8.iot.core.message.broadcast.base;

import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.base.IMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public interface IBroadcastMessage extends IMessage {
    String getAddress();

    IMessage getMessage();

    @Override
    default EMessageType getMessageType() {
        return EMessageType.BROADCAST;
    }
}
