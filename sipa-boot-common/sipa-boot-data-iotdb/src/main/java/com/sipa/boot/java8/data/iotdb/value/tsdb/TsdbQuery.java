package com.sipa.boot.java8.data.iotdb.value.tsdb;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author caszhou
 * @date 2021/6/25
 */
public class TsdbQuery {
    private LocalDateTime start;

    private LocalDateTime end;

    private String collectionId;

    private List<String> metrics;

    private ETsdbDownSample downSample = ETsdbDownSample.NONE;

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    public ETsdbDownSample getDownSample() {
        return downSample;
    }

    public void setDownSample(ETsdbDownSample downSample) {
        this.downSample = downSample;
    }

    public static final class TsdbQueryBuilder {
        private LocalDateTime start;

        private LocalDateTime end;

        private String collectionId;

        private List<String> metrics;

        private ETsdbDownSample downSample;

        private TsdbQueryBuilder() {
        }

        public static TsdbQueryBuilder aTsdbQuery() {
            return new TsdbQueryBuilder();
        }

        public TsdbQueryBuilder withStart(LocalDateTime start) {
            this.start = start;
            return this;
        }

        public TsdbQueryBuilder withEnd(LocalDateTime end) {
            this.end = end;
            return this;
        }

        public TsdbQueryBuilder withCollectionId(String collectionId) {
            this.collectionId = collectionId;
            return this;
        }

        public TsdbQueryBuilder withMetrics(List<String> metrics) {
            this.metrics = metrics;
            return this;
        }

        public TsdbQueryBuilder withDownSample(ETsdbDownSample downSample) {
            this.downSample = downSample;
            return this;
        }

        public TsdbQuery build() {
            TsdbQuery tsdbQuery = new TsdbQuery();
            tsdbQuery.setStart(start);
            tsdbQuery.setEnd(end);
            tsdbQuery.setCollectionId(collectionId);
            tsdbQuery.setMetrics(metrics);
            tsdbQuery.setDownSample(downSample);
            return tsdbQuery;
        }
    }
}
