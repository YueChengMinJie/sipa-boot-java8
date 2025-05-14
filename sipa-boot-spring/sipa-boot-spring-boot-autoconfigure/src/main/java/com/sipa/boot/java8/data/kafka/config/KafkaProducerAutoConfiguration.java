package com.sipa.boot.java8.data.kafka.config;

import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.google.common.collect.Maps;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.utils.CheckUtils;
import com.sipa.boot.java8.data.kafka.partitioner.SimplePartitioner;
import com.sipa.boot.java8.data.kafka.producer.KafkaMsgProducer;
import com.sipa.boot.java8.data.kafka.property.KafkaProducerProperties;

/**
 * @author zhouxiajie
 * @date 2019-01-15
 */
@Configuration
@EnableConfigurationProperties(KafkaProducerProperties.class)
@ConditionalOnClass(value = {KafkaTemplate.class, KafkaProducerProperties.class})
@ConditionalOnProperty(prefix = KafkaProducerProperties.PREFIX, name = "enabled", havingValue = "true")
public class KafkaProducerAutoConfiguration {
    private final KafkaProducerProperties kafkaProducerProperties;

    public KafkaProducerAutoConfiguration(KafkaProducerProperties kafkaProducerProperties) {
        this.kafkaProducerProperties = kafkaProducerProperties;
    }

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = Maps.newHashMap();

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        props.put(ProducerConfig.ACKS_CONFIG, SipaBootCommonConstants.StringValue.STRING_VALUE_0);

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CheckUtils
            .requireNonBlank(kafkaProducerProperties.getBrokerAddress(), "Kafka bootstrap servers is empty!"));

        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1024 * 1024 * 1024);

        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        if (kafkaProducerProperties.isSsl()) {
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaProducerProperties.getJksPath());

            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaProducerProperties.getTruststorePassword());
        }

        final int batchSize = 512 * 1024;
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);

        final int requestTimeoutMs = 300 * 1000;
        final int lingerMs = 100;
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, requestTimeoutMs + lingerMs);

        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);

        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 1000);

        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, batchSize * 64);

        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, SimplePartitioner.class);

        props.put(ProducerConfig.RECEIVE_BUFFER_CONFIG, -1);

        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);

        props.put(SaslConfigs.SASL_JAAS_CONFIG,
            String.format("%s required username=\"%s\" password=\"%s\";", kafkaProducerProperties.getClassname(),
                kafkaProducerProperties.getUsername(), kafkaProducerProperties.getPassword()));

        if (kafkaProducerProperties.isScram()) {
            props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");

            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        } else if (kafkaProducerProperties.isSsl()) {
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");

            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        }

        props.put(ProducerConfig.SEND_BUFFER_CONFIG, -1);

        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 10);

        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100);

        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaMsgProducer kafkaMsgProducer(KafkaTemplate<String, String> kafkaTemplate,
        KafkaProducerProperties kafkaProducerProperties) {
        return new KafkaMsgProducer(kafkaTemplate, kafkaProducerProperties);
    }
}
