package com.sipa.boot.java8.data.es.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author caszhou
 * @date 2021/10/19
 */
@Document(indexName = "time-series-demo")
public class TimeSeriesDemo {
    @Id
    private String id;

    @Field(type = FieldType.Long, name = "@timestamp")
    private long timestamp;

    @Field(type = FieldType.Long)
    private long speed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
            .append("timestamp", timestamp)
            .append("speed", speed)
            .toString();
    }

    public static final class TimeSeriesDemoBuilder {
        private String id;

        private long timestamp;

        private long speed;

        private TimeSeriesDemoBuilder() {}

        public static TimeSeriesDemoBuilder aTimeSeriesDemo() {
            return new TimeSeriesDemoBuilder();
        }

        public TimeSeriesDemoBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public TimeSeriesDemoBuilder withTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public TimeSeriesDemoBuilder withSpeed(long speed) {
            this.speed = speed;
            return this;
        }

        public TimeSeriesDemo build() {
            TimeSeriesDemo timeSeriesDemo = new TimeSeriesDemo();
            timeSeriesDemo.setId(id);
            timeSeriesDemo.setTimestamp(timestamp);
            timeSeriesDemo.setSpeed(speed);
            return timeSeriesDemo;
        }
    }
}
