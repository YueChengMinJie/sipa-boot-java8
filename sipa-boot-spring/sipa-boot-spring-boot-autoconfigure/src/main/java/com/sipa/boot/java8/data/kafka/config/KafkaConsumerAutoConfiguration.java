package com.sipa.boot.java8.data.kafka.config;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import com.google.common.collect.Maps;
import com.sipa.boot.java8.data.kafka.property.KafkaConsumerProperties;

/**
 * @author zhouxiajie
 * @date 2019-01-15
 */
@EnableKafka
@Configuration
@EnableConfigurationProperties(KafkaConsumerProperties.class)
@ConditionalOnClass(value = {KafkaConsumer.class, KafkaConsumerProperties.class})
@ConditionalOnProperty(prefix = KafkaConsumerProperties.PREFIX, name = "enabled", havingValue = "true")
public class KafkaConsumerAutoConfiguration {
    private final KafkaConsumerProperties kafkaConsumerProperties;

    public KafkaConsumerAutoConfiguration(KafkaConsumerProperties kafkaConsumerProperties) {
        this.kafkaConsumerProperties = kafkaConsumerProperties;
    }

    private Map<String, Object> consumerConfigs() {
        String brokerAddress = kafkaConsumerProperties.getBrokerAddress();

        if (StringUtils.isEmpty(brokerAddress)) {
            throw new RuntimeException("Kafka broker address is empty!");
        }

        Map<String, Object> props = Maps.newHashMap();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProperties.getBrokerAddress());

        if (kafkaConsumerProperties.isSsl()) {
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaConsumerProperties.getJksPath());

            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaConsumerProperties.getTruststorePassword());

            props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        }

        if (kafkaConsumerProperties.isScram()) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");

            props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
        } else if (kafkaConsumerProperties.isSsl()) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");

            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }

        String jaasConfig =
            String.format("%s required username=\"%s\" password=\"%s\";", kafkaConsumerProperties.getClassname(),
                kafkaConsumerProperties.getUsername(), kafkaConsumerProperties.getPassword());

        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);

        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProperties.getGroupId());

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");

        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000");

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return props;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
        kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(10 * 1000);
        return factory;
    }
}
