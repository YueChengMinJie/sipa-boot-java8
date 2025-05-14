package com.sipa.boot.java8.data.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.java8.data.kafka.common.EndOffsetLag;
import com.sipa.boot.java8.data.kafka.config.KafkaAdminAutoConfiguration;

/**
 * @author zhouxiajie
 * @date 2021/1/27
 */
@ActiveProfiles("kafka")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KafkaAdminAutoConfiguration.class)
public class KafkaAdminTest {
    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Test
    @Ignore
    public void testApi() throws ExecutionException, InterruptedException {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfig())) {
            String groupId = "flink-event-calc-consumer";
            Map<TopicPartition, OffsetAndMetadata> consumerGroupOffsets = getConsumerGroupOffsets(adminClient, groupId);
            Map<TopicPartition, Long> topicEndOffsets = getTopicEndOffsets(groupId, consumerGroupOffsets.keySet());
            Map<TopicPartition,
                EndOffsetLag> consumerGroupLag = consumerGroupOffsets.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new EndOffsetLag(topicEndOffsets.get(entry.getKey()), entry.getValue().offset())));
            Assert.assertNotNull(consumerGroupLag);
        }
    }

    private Map<TopicPartition, OffsetAndMetadata> getConsumerGroupOffsets(AdminClient adminClient, String groupId)
        throws ExecutionException, InterruptedException {
        return adminClient.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get();
    }

    private Map<TopicPartition, Long> getTopicEndOffsets(String groupId, Set<TopicPartition> partitions) {
        Map<String, Object> config = new HashMap<>(kafkaAdmin.getConfig());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config);
        return consumer.endOffsets(partitions);
    }
}
