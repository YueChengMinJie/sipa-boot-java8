package com.sipa.boot.java8.data.kafka.config;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import com.google.common.collect.Maps;
import com.sipa.boot.java8.data.kafka.property.KafkaAdminProperties;

/**
 * @author zhouxiajie
 * @date 2019-01-15
 */
@Configuration
@EnableConfigurationProperties(KafkaAdminProperties.class)
@ConditionalOnClass(value = {KafkaAdmin.class, KafkaAdminProperties.class})
@ConditionalOnProperty(prefix = KafkaAdminProperties.PREFIX, name = "enabled", havingValue = "true")
public class KafkaAdminAutoConfiguration {
    private final KafkaAdminProperties kafkaAdminProperties;

    public KafkaAdminAutoConfiguration(KafkaAdminProperties kafkaAdminProperties) {
        this.kafkaAdminProperties = kafkaAdminProperties;
    }

    private Map<String, Object> adminConfigs() {
        String brokerAddress = kafkaAdminProperties.getBrokerAddress();

        if (StringUtils.isEmpty(brokerAddress)) {
            throw new RuntimeException("Kafka broker address is empty!");
        }

        Map<String, Object> props = Maps.newHashMap();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAdminProperties.getBrokerAddress());

        if (kafkaAdminProperties.isSsl()) {
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaAdminProperties.getJksPath());

            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaAdminProperties.getTruststorePassword());

            props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        }

        if (kafkaAdminProperties.isScram()) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");

            props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
        } else if (kafkaAdminProperties.isSsl()) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");

            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }

        String jaasConfig =
            String.format("%s required username=\"%s\" password=\"%s\";", kafkaAdminProperties.getClassname(),
                kafkaAdminProperties.getUsername(), kafkaAdminProperties.getPassword());

        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);

        return props;
    }

    @Bean
    public KafkaAdmin consumerFactory() {
        return new KafkaAdmin(adminConfigs());
    }
}
