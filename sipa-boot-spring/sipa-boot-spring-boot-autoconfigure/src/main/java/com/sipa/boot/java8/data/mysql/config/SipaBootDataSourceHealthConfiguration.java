package com.sipa.boot.java8.data.mysql.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.sipa.boot.java8.data.mysql.propertity.SipaBootDynamicDataSourceProperties;

/**
 * @author zhouxiajie
 * @date 2021/3/22
 */
@Configuration
@ConditionalOnProperty(prefix = "sipa.boot.datasource", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({SipaBootDynamicDataSourceProperties.class})
public class SipaBootDataSourceHealthConfiguration extends DataSourceHealthContributorAutoConfiguration {
    @Value("${sipa.boot.datasource.validation-query:select 1}")
    private String defaultQuery;

    public SipaBootDataSourceHealthConfiguration(Map<String, DataSource> dataSources,
        ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders) {
        super(dataSources, metadataProviders);
    }

    @Override
    protected AbstractHealthIndicator createIndicator(DataSource source) {
        DataSourceHealthIndicator indicator = (DataSourceHealthIndicator)super.createIndicator(source);
        if (!StringUtils.hasText(indicator.getQuery())) {
            indicator.setQuery(defaultQuery);
        }
        return indicator;
    }
}
