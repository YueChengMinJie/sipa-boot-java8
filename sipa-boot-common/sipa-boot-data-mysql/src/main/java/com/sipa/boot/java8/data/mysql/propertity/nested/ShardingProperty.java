package com.sipa.boot.java8.data.mysql.propertity.nested;

import java.util.Properties;

/**
 * @author zhouxiajie
 * @date 2019-06-08
 */
public class ShardingProperty {
    private Properties props = new Properties();

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }
}
