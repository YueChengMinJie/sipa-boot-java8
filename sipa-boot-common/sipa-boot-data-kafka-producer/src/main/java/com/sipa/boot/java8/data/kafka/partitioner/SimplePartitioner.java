package com.sipa.boot.java8.data.kafka.partitioner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouxiajie
 * @date 2019-01-15
 */
public class SimplePartitioner implements Partitioner {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final AtomicLong increment = new AtomicLong();

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);

        int numPartitions = partitions.size();

        long partition = -1;

        if (keyBytes != null) {
            partition = toPositive(Utils.murmur2(keyBytes)) % numPartitions;
        }

        if (increment.get() == Long.MAX_VALUE) {
            increment.getAndSet(0);
        }

        long tempKey = increment.incrementAndGet();

        if (partition == -1) {
            partition = Math.abs(tempKey) % numPartitions;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SimplePartitioner.partition topic [{}], key [{}], increment [{}], partition [{}]", topic, key,
                tempKey, partition);
        }

        return new Long(partition).intValue();
    }

    @Override
    public void close() {}

    @Override
    public void configure(Map<String, ?> configs) {}

    private int toPositive(int number) {
        return number & 0x7fffffff;
    }
}
