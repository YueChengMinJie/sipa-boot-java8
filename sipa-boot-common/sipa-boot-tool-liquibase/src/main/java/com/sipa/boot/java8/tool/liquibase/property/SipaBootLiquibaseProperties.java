package com.sipa.boot.java8.tool.liquibase.property;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author caszhou
 * @date 2021/9/10
 */
@ConfigurationProperties(prefix = "sipa.boot.liquibase")
public class SipaBootLiquibaseProperties extends LiquibaseProperties {
    private String username;

    private String driverClassName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
}
