package com.sipa.boot.java8.tool.liquibase.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.sipa.boot.java8.tool.liquibase.property.SipaBootLiquibaseProperties;

import liquibase.integration.spring.SpringLiquibase;

/**
 * @author caszhou
 * @date 2021/10/11
 */
@Configuration
@ConditionalOnClass({SipaBootLiquibaseProperties.class})
@ConditionalOnProperty(prefix = "sipa.boot.liquibase", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({SipaBootLiquibaseProperties.class})
@AutoConfigureBefore(LiquibaseAutoConfiguration.class)
public class SipaBootLiquibaseAutoConfiguration {
    private final SipaBootLiquibaseProperties properties;

    public SipaBootLiquibaseAutoConfiguration(SipaBootLiquibaseProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SpringLiquibase liquibase() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(this.properties.getUrl());
        dataSource.setUsername(this.properties.getUsername());
        dataSource.setPassword(this.properties.getPassword());
        dataSource.setDriverClassName(this.properties.getDriverClassName());

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(this.properties.getChangeLog());
        liquibase.setClearCheckSums(this.properties.isClearChecksums());
        liquibase.setContexts(this.properties.getContexts());
        liquibase.setDefaultSchema(this.properties.getDefaultSchema());
        liquibase.setLiquibaseSchema(this.properties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(this.properties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(this.properties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(this.properties.getDatabaseChangeLogLockTable());
        liquibase.setDropFirst(this.properties.isDropFirst());
        liquibase.setShouldRun(this.properties.isEnabled());
        liquibase.setLabels(this.properties.getLabels());
        liquibase.setChangeLogParameters(this.properties.getParameters());
        liquibase.setRollbackFile(this.properties.getRollbackFile());
        liquibase.setTestRollbackOnUpdate(this.properties.isTestRollbackOnUpdate());
        liquibase.setTag(this.properties.getTag());
        return liquibase;
    }
}
