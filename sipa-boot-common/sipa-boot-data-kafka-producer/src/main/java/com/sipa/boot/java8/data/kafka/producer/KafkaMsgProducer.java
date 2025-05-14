package com.sipa.boot.java8.data.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.sipa.boot.java8.common.utils.JsonUtils;
import com.sipa.boot.java8.common.utils.ThreadUtils;
import com.sipa.boot.java8.data.kafka.property.KafkaProducerProperties;

/**
 * @author zhouxiajie
 * @date 2019-01-15
 */
public class KafkaMsgProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMsgProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final KafkaProducerProperties kafkaProducerProperties;

    public KafkaMsgProducer(KafkaTemplate<String, String> kafkaTemplate,
        KafkaProducerProperties kafkaProducerProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProducerProperties = kafkaProducerProperties;
    }

    public void send(String data) {
        doSend(kafkaProducerProperties.getDefaultTopic(), null, data);
    }

    public void send(final String topic, String data) {
        doSend(topic, null, data);
    }

    public void send(final String topic, String key, String data) {
        doSend(topic, key, data);
    }

    public void sendObject(Object data) {
        final String jsonData = JsonUtils.writeValueAsString(data);

        doSend(kafkaProducerProperties.getDefaultTopic(), null, jsonData);
    }

    public void sendObject(final String topic, Object data) {
        final String jsonData = JsonUtils.writeValueAsString(data);

        doSend(topic, null, jsonData);
    }

    public void sendObject(final String topic, String key, Object data) {
        final String jsonData = JsonUtils.writeValueAsString(data);

        doSend(topic, key, jsonData);
    }

    private void doSend(String topic, String key, String data) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("kafka producer topic [{}], key [{}], data [{}]", topic, key, data);
        }

        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, data);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("kafka sent topic [{}] data [{}] with offset [{}] partition [{}]", topic, data,
                        result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
                }
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("kafka unable to send to topic [{}], data [{}]", topic, data, ex);

                kafkaTemplate.send(topic, key, data);

                ThreadUtils.sleepQuitly(1000);
            }
        });
    }

    public KafkaTemplate<String, String> getKafkaTemplate() {
        return kafkaTemplate;
    }

    public KafkaProducerProperties getKafkaProducerProperties() {
        return kafkaProducerProperties;
    }
}
