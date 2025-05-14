package com.sipa.boot.java8.data.iotdb.property;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
@ConfigurationProperties(prefix = "sipa.boot.iotdb")
public class IotdbProperties {
    @NestedConfigurationProperty
    private List<IotdbServerProperties> servers;

    @NestedConfigurationProperty
    private IotdbSessionProperties session = new IotdbSessionProperties();

    @NestedConfigurationProperty
    private IotdbJdbcProperties jdbc = new IotdbJdbcProperties();

    @NestedConfigurationProperty
    private IotdbOtherProperties other = new IotdbOtherProperties();

    public List<IotdbServerProperties> getServers() {
        return servers;
    }

    public void setServers(List<IotdbServerProperties> servers) {
        this.servers = servers;
    }

    public IotdbSessionProperties getSession() {
        return session;
    }

    public void setSession(IotdbSessionProperties session) {
        this.session = session;
    }

    public IotdbJdbcProperties getJdbc() {
        return jdbc;
    }

    public void setJdbc(IotdbJdbcProperties jdbc) {
        this.jdbc = jdbc;
    }

    public IotdbOtherProperties getOther() {
        return other;
    }

    public void setOther(IotdbOtherProperties other) {
        this.other = other;
    }
}
