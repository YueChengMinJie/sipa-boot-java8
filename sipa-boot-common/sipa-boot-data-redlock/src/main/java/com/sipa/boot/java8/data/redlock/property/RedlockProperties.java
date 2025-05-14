package com.sipa.boot.java8.data.redlock.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xuanyu
 * @date 2019-07-24
 */
@ConfigurationProperties(prefix = "sipa.boot.redlock")
@Component
public class RedlockProperties {
    private String address;

    private String password;

    private Integer db;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDb() {
        return db;
    }

    public void setDb(Integer db) {
        this.db = db;
    }
}
