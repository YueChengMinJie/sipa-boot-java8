package com.sipa.boot.java8.iot.core.property;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class IotDeviceProperties {
    @NestedConfigurationProperty
    private IotDeviceMessageProperties message = new IotDeviceMessageProperties();

    public IotDeviceMessageProperties getMessage() {
        return message;
    }

    public void setMessage(IotDeviceMessageProperties message) {
        this.message = message;
    }

    public static class IotDeviceMessageProperties {
        private Integer defaultTimeout;

        public Integer getDefaultTimeout() {
            return defaultTimeout;
        }

        public void setDefaultTimeout(Integer defaultTimeout) {
            this.defaultTimeout = defaultTimeout;
        }
    }
}
