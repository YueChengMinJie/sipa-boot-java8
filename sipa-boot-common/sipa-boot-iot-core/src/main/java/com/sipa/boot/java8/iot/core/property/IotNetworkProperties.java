package com.sipa.boot.java8.iot.core.property;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author caszhou
 * @date 2021/9/30
 */
public class IotNetworkProperties {
    @NestedConfigurationProperty
    private IotNetworkMqttProperties mqtt = new IotNetworkMqttProperties();

    @NestedConfigurationProperty
    private IotNetworkTcpProperties tcp = new IotNetworkTcpProperties();

    public IotNetworkMqttProperties getMqtt() {
        return mqtt;
    }

    public void setMqtt(IotNetworkMqttProperties mqtt) {
        this.mqtt = mqtt;
    }

    public IotNetworkTcpProperties getTcp() {
        return tcp;
    }

    public void setTcp(IotNetworkTcpProperties tcp) {
        this.tcp = tcp;
    }

    public static class IotNetworkMqttProperties {

    }

    public static class IotNetworkTcpProperties {
        private String host;

        private int port;

        private int keepAliveTimeout;

        private boolean ssl;

        private String certId;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getKeepAliveTimeout() {
            return keepAliveTimeout;
        }

        public void setKeepAliveTimeout(int keepAliveTimeout) {
            this.keepAliveTimeout = keepAliveTimeout;
        }

        public boolean isSsl() {
            return ssl;
        }

        public void setSsl(boolean ssl) {
            this.ssl = ssl;
        }

        public String getCertId() {
            return certId;
        }

        public void setCertId(String certId) {
            this.certId = certId;
        }
    }
}
