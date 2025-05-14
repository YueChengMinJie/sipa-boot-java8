package com.sipa.boot.java8.data.mongodb.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.google.common.collect.Lists;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.connection.*;
import com.sipa.boot.java8.data.mongodb.convert.ieum.EnumReadFactory;
import com.sipa.boot.java8.data.mongodb.convert.ieum.EnumWriterConverter;
import com.sipa.boot.java8.data.mongodb.page.MongoPageHelper;
import com.sipa.boot.java8.data.mongodb.property.MongodbProperties;

/**
 * @author feizhihao
 * @date 2019/12/13 9:44 上午
 */
@Configuration
@ConditionalOnClass(MongodbProperties.class)
@ComponentScan(value = {"com.sipa.boot.java8.data.mongodb.**"})
public class MongodbAutoConfiguration extends AbstractMongoClientConfiguration {
    private final MongodbProperties properties;

    public MongodbAutoConfiguration(MongodbProperties properties) {
        this.properties = properties;
    }

    @Nonnull
    @Override
    protected String getDatabaseName() {
        return properties.getDatabase();
    }

    @Override
    protected void configureClientSettings(@Nonnull MongoClientSettings.Builder builder) {
        super.configureClientSettings(builder);

        builder.applyToServerSettings(b -> b.applySettings(ServerSettings.builder()
            .minHeartbeatFrequency(properties.getMinHeartbeatFrequency(), TimeUnit.MILLISECONDS)
            .heartbeatFrequency(properties.getHeartbeatFrequency(), TimeUnit.MILLISECONDS)
            .build()));

        builder.applyToClusterSettings(b -> {
            ClusterSettings.Builder clusterSettingsBuilder = ClusterSettings.builder();

            if (isReplicaSet()) {
                clusterSettingsBuilder.requiredClusterType(ClusterType.REPLICA_SET);
                clusterSettingsBuilder.requiredReplicaSetName(properties.getReplicaSet());
            } else {
                clusterSettingsBuilder.requiredClusterType(ClusterType.STANDALONE);
            }

            clusterSettingsBuilder.hosts(getServerAddressList());

            clusterSettingsBuilder.localThreshold(properties.getLocalThreshold(), TimeUnit.MILLISECONDS);

            b.applySettings(clusterSettingsBuilder
                .serverSelectionTimeout(properties.getServerSelectionTimeout(), TimeUnit.MILLISECONDS)
                .build());
        });

        builder.applyToConnectionPoolSettings(b -> b.applySettings(ConnectionPoolSettings.builder()
            .maxWaitTime(properties.getMaxWaitTime(), TimeUnit.MILLISECONDS)
            .maxConnectionIdleTime(properties.getMaxConnectionIdleTime(), TimeUnit.MILLISECONDS)
            .maxConnectionLifeTime(properties.getMaxConnectionLifeTime(), TimeUnit.MILLISECONDS)
            .build()));

        builder.applyToSslSettings(b -> b.applySettings(SslSettings.builder()
            .enabled(properties.getSslEnabled())
            .invalidHostNameAllowed(properties.getInvalidHostNameAllowed())
            .build()));

        builder.applyToSocketSettings(b -> b.applySettings(SocketSettings.builder()
            .connectTimeout(properties.getConnectTimeout(), TimeUnit.MILLISECONDS)
            .readTimeout(properties.getReadTimeout(), TimeUnit.MILLISECONDS)
            .build()));

        if (needCredential()) {
            builder.credential(getCredential());
        }
    }

    private MongoCredential getCredential() {
        return MongoCredential.createScramSha1Credential(properties.getUsername(), getSource(),
            properties.getPassword().toCharArray());
    }

    private String getSource() {
        return properties.getAuthenticationDatabase() != null ? properties.getAuthenticationDatabase()
            : properties.getDatabase();
    }

    private boolean needCredential() {
        return StringUtils.isNotBlank(properties.getUsername());
    }

    private boolean isReplicaSet() {
        return StringUtils.isNotBlank(properties.getReplicaSet());
    }

    private List<ServerAddress> getServerAddressList() {
        List<ServerAddress> serverAddresses = Lists.newArrayList();
        for (String address : properties.getAddress()) {
            String[] hostAndPort = address.split(":");
            String host = hostAndPort[0];
            if (hostAndPort.length > 1) {
                int port = Integer.parseInt(hostAndPort[1]);
                ServerAddress serverAddress = new ServerAddress(host, port);
                serverAddresses.add(serverAddress);
            } else {
                ServerAddress serverAddress = new ServerAddress(host);
                serverAddresses.add(serverAddress);
            }
        }
        return serverAddresses;
    }

    @Bean
    @Nonnull
    @Override
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new EnumWriterConverter());
        converters.add(new EnumReadFactory.EnumReadConverter<>(null));
        return new MongoCustomConversions(converters);
    }

    @Bean
    @Nonnull
    @Override
    public MongoTemplate mongoTemplate(@Nonnull MongoDatabaseFactory mongoDatabaseFactory,
        MappingMongoConverter mappingMongoConverter) {
        mappingMongoConverter.setMapKeyDotReplacement("__DOT__");

        ConversionService conversionService = mappingMongoConverter.getConversionService();
        ((GenericConversionService)conversionService).addConverterFactory(new EnumReadFactory());
        mappingMongoConverter.afterPropertiesSet();

        return new MongoTemplate(mongoDatabaseFactory, mappingMongoConverter);
    }

    @Bean
    public MongoPageHelper mongoPageHelper(MongoTemplate mongoTemplate) {
        return new MongoPageHelper(mongoTemplate);
    }
}
