package com.sipa.boot.java8.data.mysql.config;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.core.yaml.swapper.MasterSlaveRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.core.yaml.swapper.ShardingRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.encrypt.yaml.swapper.EncryptRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.shardingjdbc.api.EncryptDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.shardingsphere.spring.boot.util.DataSourceUtil;
import org.apache.shardingsphere.spring.boot.util.PropertyUtil;
import org.apache.shardingsphere.underlying.common.config.inline.InlineExpressionParser;
import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.google.common.base.Preconditions;
import com.sipa.boot.java8.data.mysql.SipaBootDynamicRoutingDataSource;
import com.sipa.boot.java8.data.mysql.propertity.SipaBootDynamicDataSourceProperties;
import com.sipa.boot.java8.data.mysql.propertity.nested.ShardingProperty;
import com.sipa.boot.java8.data.mysql.propertity.nested.SharingDataSource;
import com.sipa.boot.java8.data.mysql.provider.SipaBootDynamicDataSourceProvider;

/**
 * @author zhouxiajie
 * @date 2019-06-08
 */
@Configuration
@ConditionalOnProperty(prefix = "sipa.boot.datasource", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({SipaBootDynamicDataSourceProperties.class})
@AutoConfigureBefore(DynamicDataSourceAutoConfiguration.class)
public class SipaBootDynamicDataSourceAutoConfiguration implements EnvironmentAware {
    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

    private final ShardingRuleConfigurationYamlSwapper shardingSwapper = new ShardingRuleConfigurationYamlSwapper();

    private final MasterSlaveRuleConfigurationYamlSwapper masterSlaveSwapper =
        new MasterSlaveRuleConfigurationYamlSwapper();

    private final EncryptRuleConfigurationYamlSwapper encryptSwapper = new EncryptRuleConfigurationYamlSwapper();

    private final SipaBootDynamicDataSourceProperties dynamicDataSourceProperties;

    private final DynamicDataSourceProperties properties;

    public SipaBootDynamicDataSourceAutoConfiguration(SipaBootDynamicDataSourceProperties dynamicDataSourceProperties,
        DynamicDataSourceProperties properties) {
        this.dynamicDataSourceProperties = dynamicDataSourceProperties;
        this.properties = properties;
    }

    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        return new SipaBootDynamicDataSourceProvider(dataSourceMap);
    }

    @Bean
    public DataSource dataSource(DynamicDataSourceProvider dynamicDataSourceProvider) {
        DynamicRoutingDataSource dataSource = new SipaBootDynamicRoutingDataSource();
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setStrategy(properties.getStrategy());
        dataSource.setProvider(dynamicDataSourceProvider);
        dataSource.setP6spy(properties.getP6spy());
        dataSource.setStrict(properties.getStrict());
        dataSource.setSeata(properties.getSeata());
        return dataSource;
    }

    @Override
    public void setEnvironment(Environment environment) {
        List<String> dbs = dynamicDataSourceProperties.getNames();

        if (CollectionUtils.isNotEmpty(dbs)) {
            StandardEnvironment standardEnv = getStandardEnvironment(environment);

            for (String db : dbs) {
                String dbPrefix = String.format("sipa.boot.datasource.dynamics.%s", db);

                String prefix = dbPrefix + ".datasource.";

                SharingDataSource dynamic = Binder.get(standardEnv).bind(dbPrefix, SharingDataSource.class).get();

                dataSourceMap.put(db,
                    dataSource(dataSourceMap(standardEnv, prefix), dynamic, dynamicDataSourceProperties.getProps()));
            }
        } else {
            throw new ShardingSphereException("Can't find [sipa.boot.datasource.names]!");
        }
    }

    private StandardEnvironment getStandardEnvironment(Environment environment) {
        StandardEnvironment standardEnv = (StandardEnvironment)environment;
        standardEnv.setIgnoreUnresolvableNestedPlaceholders(true);
        return standardEnv;
    }

    public DataSource dataSource(Map<String, DataSource> dataSourceMap, SharingDataSource dynamic,
        ShardingProperty shardingProperty) {
        if (null != dynamic.getMasterslave() && null != dynamic.getMasterslave().getMasterDataSourceName()) {
            try {
                return MasterSlaveDataSourceFactory.createDataSource(dataSourceMap,
                    masterSlaveSwapper.swap(dynamic.getMasterslave()), shardingProperty.getProps());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (dynamic.getEncrypt() != null && !dynamic.getEncrypt().getEncryptors().isEmpty()) {
            try {
                return EncryptDataSourceFactory.createDataSource(dataSourceMap.values().iterator().next(),
                    encryptSwapper.swap(dynamic.getEncrypt()), null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return ShardingDataSourceFactory.createDataSource(dataSourceMap,
                shardingSwapper.swap(dynamic.getSharding()), shardingProperty.getProps());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, DataSource> dataSourceMap(StandardEnvironment standardEnv, String prefix) {
        Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

        for (String each : getDataSourceNames(standardEnv, prefix)) {
            try {
                dataSourceMap.put(each, getDataSource(standardEnv, prefix, each));
            } catch (final ReflectiveOperationException ex) {
                throw new ShardingSphereException("Can't find datasource type!", ex);
            }
        }

        return dataSourceMap;
    }

    private List<String> getDataSourceNames(final StandardEnvironment standardEnv, final String prefix) {
        return null == standardEnv.getProperty(prefix + "name")
            ? new InlineExpressionParser(standardEnv.getProperty(prefix + "names")).splitAndEvaluate()
            : Collections.singletonList(standardEnv.getProperty(prefix + "name"));
    }

    @SuppressWarnings("unchecked")
    private DataSource getDataSource(final Environment environment, final String prefix, final String dataSourceName)
        throws ReflectiveOperationException {
        Map<String, Object> dataSourceProps =
            PropertyUtil.handle(environment, prefix + dataSourceName.trim(), Map.class);
        Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
        return DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
    }
}
