package com.sipa.boot.java8.iot.core.server.monitor;

import org.springframework.stereotype.Component;

import com.sipa.boot.java8.iot.core.property.IotProperties;
import com.sipa.boot.java8.iot.core.server.monitor.base.IGatewayServerMetrics;
import com.sipa.boot.java8.iot.core.server.monitor.base.IGatewayServerMonitor;

/**
 * @author caszhou
 * @date 2021/10/3
 */
@Component
public class DefaultGatewayServerMonitor implements IGatewayServerMonitor {
    private final IotProperties properties;

    public DefaultGatewayServerMonitor(IotProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getCurrentServerId() {
        return properties.getServerId();
    }

    @Override
    public IGatewayServerMetrics metrics() {
        // todo support gateway metrics monitor
        return null;
    }
}
