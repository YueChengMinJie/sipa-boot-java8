package com.sipa.boot.java8.iot.core.device;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.sipa.boot.java8.iot.core.config.base.IConfigKey;
import com.sipa.boot.java8.iot.core.config.base.IConfigKeyValue;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class DeviceInfo implements Serializable {
    private static final long serialVersionUID = -6849794470754667710L;

    /**
     * 设备ID
     */
    private String id;

    /**
     * 产品-型号ID
     */
    private String productId;

    /**
     * 消息协议
     */
    private String protocol;

    /**
     * 物模型
     */
    private String metadata;

    /**
     * 其他配置
     */
    private Map<String, Object> configuration = new HashMap<>();

    public DeviceInfo() {}

    public DeviceInfo(String id, String productId, String protocol, String metadata,
        Map<String, Object> configuration) {
        this.id = id;
        this.productId = productId;
        this.protocol = protocol;
        this.metadata = metadata;
        this.configuration = configuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    public DeviceInfo addConfig(String key, Object value) {
        if (configuration == null) {
            configuration = new HashMap<>();
        }
        configuration.put(key, value);
        return this;
    }

    public DeviceInfo addConfigs(Map<String, ?> configs) {
        if (configs == null) {
            return this;
        }
        configs.forEach(this::addConfig);
        return this;
    }

    public <T> DeviceInfo addConfig(IConfigKey<T> key, T value) {
        addConfig(key.getKey(), value);
        return this;
    }

    public <T> DeviceInfo addConfig(IConfigKeyValue<T> keyValue) {
        addConfig(keyValue.getKey(), keyValue.getValue());
        return this;
    }

    public static final class DeviceInfoBuilder {
        private String id;

        private String productId;

        private String protocol;

        private String metadata;

        private Map<String, Object> configuration = new HashMap<>();

        private DeviceInfoBuilder() {}

        public static DeviceInfoBuilder aDeviceInfo() {
            return new DeviceInfoBuilder();
        }

        public DeviceInfoBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public DeviceInfoBuilder withProductId(String productId) {
            this.productId = productId;
            return this;
        }

        public DeviceInfoBuilder withProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public DeviceInfoBuilder withMetadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        public DeviceInfoBuilder withConfiguration(Map<String, Object> configuration) {
            this.configuration = configuration;
            return this;
        }

        public DeviceInfo build() {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setId(id);
            deviceInfo.setProductId(productId);
            deviceInfo.setProtocol(protocol);
            deviceInfo.setMetadata(metadata);
            deviceInfo.setConfiguration(configuration);
            return deviceInfo;
        }
    }
}
