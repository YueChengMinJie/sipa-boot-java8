package com.sipa.boot.java8.data.mysql.propertity;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.data.mysql.propertity.nested.ShardingProperty;

/**
 * @author zhouxiajie
 * @date 2019-06-08
 */
@ConfigurationProperties(prefix = "sipa.boot.datasource")
@Component
public class SipaBootDynamicDataSourceProperties {
    private boolean enabled;

    private List<String> names;

    private ShardingProperty props = new ShardingProperty();

    public ShardingProperty getProps() {
        return props;
    }

    public void setProps(ShardingProperty props) {
        this.props = props;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
