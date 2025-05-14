package com.sipa.boot.java8.iot.core.protocol.management;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class ProtocolSupportDefinition implements Serializable {
    private static final long serialVersionUID = -1;

    private String id;

    private String name;

    private String description;

    private String provider;

    private byte state;

    private Map<String, Object> configuration;

    public ProtocolSupportDefinition() {
    }

    public ProtocolSupportDefinition(String id, String name, String description, String provider, byte state,
        Map<String, Object> configuration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.provider = provider;
        this.state = state;
        this.configuration = configuration;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
            .append("name", name)
            .append("description", description)
            .append("provider", provider)
            .append("state", state)
            .append("configuration", configuration)
            .toString();
    }

    public static final class ProtocolSupportDefinitionBuilder {
        private String id;

        private String name;

        private String description;

        private String provider;

        private byte state;

        private Map<String, Object> configuration;

        private ProtocolSupportDefinitionBuilder() {}

        public static ProtocolSupportDefinitionBuilder aProtocolSupportDefinition() {
            return new ProtocolSupportDefinitionBuilder();
        }

        public ProtocolSupportDefinitionBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ProtocolSupportDefinitionBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ProtocolSupportDefinitionBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ProtocolSupportDefinitionBuilder withProvider(String provider) {
            this.provider = provider;
            return this;
        }

        public ProtocolSupportDefinitionBuilder withState(byte state) {
            this.state = state;
            return this;
        }

        public ProtocolSupportDefinitionBuilder withConfiguration(Map<String, Object> configuration) {
            this.configuration = configuration;
            return this;
        }

        public ProtocolSupportDefinition build() {
            ProtocolSupportDefinition protocolSupportDefinition = new ProtocolSupportDefinition();
            protocolSupportDefinition.setId(id);
            protocolSupportDefinition.setName(name);
            protocolSupportDefinition.setDescription(description);
            protocolSupportDefinition.setProvider(provider);
            protocolSupportDefinition.setState(state);
            protocolSupportDefinition.setConfiguration(configuration);
            return protocolSupportDefinition;
        }
    }
}
