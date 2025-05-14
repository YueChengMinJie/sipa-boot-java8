package com.sipa.boot.java8.iot.core.server.session;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;

import javax.annotation.Nullable;

import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.enumerate.EErrorCode;
import com.sipa.boot.java8.iot.core.exception.DeviceOperationException;
import com.sipa.boot.java8.iot.core.message.codec.base.IEncodedMessage;
import com.sipa.boot.java8.iot.core.message.codec.base.ITransport;
import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSession;
import com.sipa.boot.java8.iot.core.server.session.base.IPersistentSession;
import com.sipa.boot.java8.iot.core.server.session.base.IReplaceableDeviceSession;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class KeepOnlineSession implements IDeviceSession, IReplaceableDeviceSession, IPersistentSession {
    IDeviceSession parent;

    private long lastKeepAliveTime = System.currentTimeMillis();

    private final long connectTime = System.currentTimeMillis();

    private long keepAliveTimeOutMs;

    public KeepOnlineSession(IDeviceSession parent, Duration keepAliveTimeOut) {
        this.parent = parent;
        setKeepAliveTimeout(keepAliveTimeOut);
    }

    void setLastKeepAliveTime(long lastKeepAliveTime) {
        this.lastKeepAliveTime = lastKeepAliveTime;
    }

    @Override
    public String getId() {
        return parent.getId();
    }

    @Override
    public String getDeviceId() {
        return parent.getDeviceId();
    }

    @Nullable
    @Override
    public IDeviceOperator getOperator() {
        return parent.getOperator();
    }

    @Override
    public long lastPingTime() {
        return lastKeepAliveTime;
    }

    @Override
    public long connectTime() {
        return connectTime;
    }

    @Override
    public Mono<Boolean> send(IEncodedMessage encodedMessage) {
        return Mono.defer(() -> {
            if (parent.isAlive()) {
                return parent.send(encodedMessage);
            }
            return Mono.error(new DeviceOperationException(EErrorCode.CONNECTION_LOST));
        });
    }

    @Override
    public ITransport getTransport() {
        return parent.getTransport();
    }

    @Override
    public void close() {
        parent.close();
    }

    @Override
    public void ping() {
        lastKeepAliveTime = System.currentTimeMillis();
        parent.keepAlive();
    }

    @Override
    public boolean isAlive() {
        return keepAliveTimeOutMs <= 0 || System.currentTimeMillis() - lastKeepAliveTime < keepAliveTimeOutMs
            || parent.isAlive();
    }

    @Override
    public void onClose(Runnable call) {
        parent.onClose(call);
    }

    @Override
    public Optional<String> getServerId() {
        return parent.getServerId();
    }

    @Override
    public Optional<InetSocketAddress> getClientAddress() {
        return parent.getClientAddress();
    }

    @Override
    public void setKeepAliveTimeout(Duration timeout) {
        keepAliveTimeOutMs = timeout.toMillis();
        parent.setKeepAliveTimeout(timeout);
    }

    @Override
    public Duration getKeepAliveTimeout() {
        return Duration.ofMillis(keepAliveTimeOutMs);
    }

    @Override
    public boolean isWrapFrom(Class<?> type) {
        return type == KeepOnlineSession.class || parent.isWrapFrom(type);
    }

    @Override
    public <T extends IDeviceSession> T unwrap(Class<T> type) {
        return type == KeepOnlineSession.class ? type.cast(this) : parent.unwrap(type);
    }

    @Override
    public void replaceWith(IDeviceSession session) {
        this.parent = session;
    }

    @Override
    public String getProvider() {
        return KeepOnlineDeviceSessionProvider.ID;
    }
}
