package com.sipa.boot.java8.iot.core.property;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class IotProtocolProperties {
    @NestedConfigurationProperty
    private IotProtocolSpiProperties spi = new IotProtocolSpiProperties();

    public IotProtocolSpiProperties getMessage() {
        return spi;
    }

    public void setMessage(IotProtocolSpiProperties spi) {
        this.spi = spi;
    }

    public static class IotProtocolSpiProperties {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
