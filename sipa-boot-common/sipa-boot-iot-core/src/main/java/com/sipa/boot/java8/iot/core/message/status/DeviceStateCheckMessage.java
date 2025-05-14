package com.sipa.boot.java8.iot.core.message.status;

import com.sipa.boot.java8.common.archs.snowflake.IUidGenerator;
import com.sipa.boot.java8.common.utils.AppUtils;
import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IRepayableDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class DeviceStateCheckMessage extends CommonDeviceMessage
    implements IRepayableDeviceMessage<DeviceStateCheckMessageReply> {
    public static DeviceStateCheckMessage create(String deviceId) {
        DeviceStateCheckMessage message = new DeviceStateCheckMessage();
        message.setDeviceId(deviceId);
        message.setMessageId(AppUtils.getBean(IUidGenerator.class).nextSid());
        return message;
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.STATE_CHECK;
    }

    @Override
    public DeviceStateCheckMessageReply newReply() {
        return new DeviceStateCheckMessageReply().from(this);
    }
}
