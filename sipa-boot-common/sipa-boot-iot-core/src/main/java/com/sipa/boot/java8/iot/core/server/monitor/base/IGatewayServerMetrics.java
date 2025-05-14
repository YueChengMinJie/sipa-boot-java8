package com.sipa.boot.java8.iot.core.server.monitor.base;

import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSession;

/**
 * @author caszhou
 * @date 2021/10/3
 */
public interface IGatewayServerMetrics {
    void reportSession(String transport, int sessionTotal);

    void newConnection(String transport);

    void acceptedConnection(String transport);

    void rejectedConnection(String transport);

    void receiveFromDeviceMessage(IDeviceSession session);
}
