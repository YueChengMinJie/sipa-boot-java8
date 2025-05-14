package com.sipa.boot.java8.iot.core.server.monitor.base;

/**
 * @author caszhou
 * @date 2021/10/3
 */
public interface IGatewayServerMonitor {
    String getCurrentServerId();

    IGatewayServerMetrics metrics();
}
