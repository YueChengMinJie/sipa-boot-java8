package com.sipa.boot.java8.iot.core.property;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
@ConfigurationProperties(prefix = "sipa.boot.iot")
public class IotProperties {
    private String clusterName = "default";

    private String serverId;

    private Map<String, Long> transportLimit;

    @NestedConfigurationProperty
    private IotNetworkProperties network = new IotNetworkProperties();

    @NestedConfigurationProperty
    private IotDeviceProperties device = new IotDeviceProperties();

    @NestedConfigurationProperty
    private IotProtocolProperties protocol = new IotProtocolProperties();

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public IotNetworkProperties getNetwork() {
        return network;
    }

    public void setNetwork(IotNetworkProperties network) {
        this.network = network;
    }

    public IotDeviceProperties getDevice() {
        return device;
    }

    public void setDevice(IotDeviceProperties device) {
        this.device = device;
    }

    public IotProtocolProperties getProtocol() {
        return protocol;
    }

    public void setProtocol(IotProtocolProperties protocol) {
        this.protocol = protocol;
    }

    public Map<String, Long> getTransportLimit() {
        return transportLimit;
    }

    public void setTransportLimit(Map<String, Long> transportLimit) {
        this.transportLimit = transportLimit;
    }
}
