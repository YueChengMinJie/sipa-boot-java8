package com.sipa.boot.java8.iot.core.server.session;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.message.codec.base.IEncodedMessage;
import com.sipa.boot.java8.iot.core.message.codec.base.ITransport;
import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSession;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class ChildrenDeviceSession implements IDeviceSession {
    private static final Log log = LogFactory.get(ChildrenDeviceSession.class);

    private final String id;

    private final String deviceId;

    private final IDeviceSession parent;

    private final IDeviceOperator operator;

    private List<Runnable> closeListener;

    private long lastKeepAliveTime;

    private long keepAliveTimeOutMs = -1;

    public ChildrenDeviceSession(String deviceId, IDeviceSession parent, IDeviceOperator operator) {
        this.id = deviceId;
        this.parent = parent;
        this.operator = operator;
        this.deviceId = deviceId;
        this.lastKeepAliveTime = parent.lastPingTime();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Nullable
    @Override
    public IDeviceOperator getOperator() {
        return operator;
    }

    @Override
    public long lastPingTime() {
        return lastKeepAliveTime;
    }

    @Override
    public long connectTime() {
        return parent.connectTime();
    }

    @Override
    public Mono<Boolean> send(IEncodedMessage encodedMessage) {
        log.info("send child device[{}:{}] message", parent.getDeviceId(), deviceId);
        return parent.send(encodedMessage);
    }

    @Override
    public ITransport getTransport() {
        return parent.getTransport();
    }

    @Override
    public void close() {
        if (null != closeListener) {
            closeListener.forEach(Runnable::run);
        }
    }

    @Override
    public void ping() {
        parent.ping();
        this.lastKeepAliveTime = System.currentTimeMillis();
    }

    @Override
    public boolean isAlive() {
        if (keepAliveTimeOutMs <= 0) {
            return parent.isAlive();
        }
        return System.currentTimeMillis() - lastKeepAliveTime < keepAliveTimeOutMs && parent.isAlive();
    }

    @Override
    public synchronized void onClose(Runnable call) {
        if (closeListener == null) {
            closeListener = new CopyOnWriteArrayList<>();
        }
        closeListener.add(call);
    }

    @Override
    public Optional<String> getServerId() {
        return parent.getServerId();
    }

    @Override
    public boolean isWrapFrom(Class<?> type) {
        return type == ChildrenDeviceSession.class || parent.isWrapFrom(type);
    }

    @Override
    public Optional<InetSocketAddress> getClientAddress() {
        return parent.getClientAddress();
    }

    @Override
    public void setKeepAliveTimeout(Duration timeout) {
        keepAliveTimeOutMs = timeout.toMillis();
    }

    @Override
    public Duration getKeepAliveTimeout() {
        return Duration.ofMillis(keepAliveTimeOutMs);
    }

    @Override
    public <T extends IDeviceSession> T unwrap(Class<T> type) {
        return type == ChildrenDeviceSession.class ? type.cast(this) : parent.unwrap(type);
    }

    @Override
    public String toString() {
        return "children device[" + deviceId + "] in " + parent;
    }
}
