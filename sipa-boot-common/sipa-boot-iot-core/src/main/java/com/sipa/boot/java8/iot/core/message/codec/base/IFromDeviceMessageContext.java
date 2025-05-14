package com.sipa.boot.java8.iot.core.message.codec.base;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.device.base.IDeviceRegistry;
import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSession;

import reactor.core.publisher.Mono;

/**
 * 来自设备的消息上下文，可以通过此上下文获取设备会话
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IFromDeviceMessageContext extends IMessageDecodeContext {
    IDeviceSession getSession();

    @Override
    default IDeviceOperator getDevice() {
        return getSession().getOperator();
    }

    static IFromDeviceMessageContext of(IDeviceSession session, IEncodedMessage message) {
        return new IFromDeviceMessageContext() {
            @Override
            public IDeviceSession getSession() {
                return session;
            }

            @Nonnull
            @Override
            public IEncodedMessage getMessage() {
                return message;
            }
        };
    }

    static IFromDeviceMessageContext of(IDeviceSession session, IEncodedMessage message, IDeviceRegistry registry) {
        return new IFromDeviceMessageContext() {
            @Override
            public IDeviceSession getSession() {
                return session;
            }

            @Nonnull
            @Override
            public IEncodedMessage getMessage() {
                return message;
            }

            @Override
            public Mono<IDeviceOperator> getDevice(String deviceId) {
                return registry.getDevice(deviceId);
            }
        };
    }
}
