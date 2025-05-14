package com.sipa.boot.java8.iot.core.device;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.sipa.boot.java8.iot.core.config.base.IConfigKey;
import com.sipa.boot.java8.iot.core.config.base.IConfigKeyValue;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class ProductInfo implements Serializable {
    private static final long serialVersionUID = -6849794470754667710L;

    /**
     * 设备ID
     */
    private String id;

    /**
     * 消息协议
     */
    private String protocol;

    /**
     * 元数据
     */
    private String metadata;

    /**
     * 其他配置
     */
    private Map<String, Object> configuration = new HashMap<>();

    public ProductInfo() {}

    public ProductInfo(String id, String protocol, String metadata, Map<String, Object> configuration) {
        this.id = id;
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

    public ProductInfo(String id, String protocol, String metadata) {
        this.id = id;
        this.protocol = protocol;
        this.metadata = metadata;
    }

    public ProductInfo addConfig(String key, Object value) {
        if (StringUtils.isEmpty(value)) {
            return this;
        }
        if (configuration == null) {
            configuration = new HashMap<>();
        }
        configuration.put(key, value);
        return this;
    }

    public ProductInfo addConfigs(Map<String, ?> configs) {
        if (configs == null) {
            return this;
        }
        configs.forEach(this::addConfig);
        return this;
    }

    public <T> ProductInfo addConfig(IConfigKey<T> key, T value) {
        addConfig(key.getKey(), value);
        return this;
    }

    public <T> ProductInfo addConfig(IConfigKeyValue<T> keyValue) {
        addConfig(keyValue.getKey(), keyValue.getValue());
        return this;
    }
}
