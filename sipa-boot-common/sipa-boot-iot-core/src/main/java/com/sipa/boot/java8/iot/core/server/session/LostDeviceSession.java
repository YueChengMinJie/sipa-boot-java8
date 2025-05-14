package com.sipa.boot.java8.iot.core.server.session;

import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.enumerate.EErrorCode;
import com.sipa.boot.java8.iot.core.exception.DeviceOperationException;
import com.sipa.boot.java8.iot.core.message.codec.base.IEncodedMessage;
import com.sipa.boot.java8.iot.core.message.codec.base.ITransport;
import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSession;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class LostDeviceSession implements IDeviceSession {
    private final String id;

    private final IDeviceOperator operator;

    private final ITransport transport;

    public LostDeviceSession(String id, IDeviceOperator operator, ITransport transport) {
        this.id = id;
        this.operator = operator;
        this.transport = transport;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public IDeviceOperator getOperator() {
        return operator;
    }

    @Override
    public ITransport getTransport() {
        return transport;
    }

    @Override
    public String getDeviceId() {
        return operator.getDeviceId();
    }

    @Override
    public long lastPingTime() {
        return -1;
    }

    @Override
    public long connectTime() {
        return -1;
    }

    @Override
    public Mono<Boolean> send(IEncodedMessage encodedMessage) {
        return Mono.error(new DeviceOperationException(EErrorCode.CONNECTION_LOST));
    }

    @Override
    public void close() {
    }

    @Override
    public void ping() {
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public void onClose(Runnable call) {
    }
}
