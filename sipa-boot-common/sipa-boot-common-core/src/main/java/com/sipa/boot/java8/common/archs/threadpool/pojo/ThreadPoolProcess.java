package com.sipa.boot.java8.common.archs.threadpool.pojo;

import java.util.List;
import java.util.Map;

/**
 * @author zhouxiajie
 * @date 2020/6/3
 */
public class ThreadPoolProcess<T> {
    private List<T> items;

    private Integer index;

    private Integer partitionSize;

    private Map<String, String> metadata;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Integer getPartitionSize() {
        return partitionSize;
    }

    public void setPartitionSize(Integer partitionSize) {
        this.partitionSize = partitionSize;
    }

    public static final class ThreadPoolProcessBuilder<T> {
        private List<T> items;

        private Integer index;

        private Integer partitionSize;

        private Map<String, String> metadata;

        private ThreadPoolProcessBuilder() {}

        public static <T> ThreadPoolProcessBuilder<T> aThreadPoolProcess() {
            return new ThreadPoolProcessBuilder<>();
        }

        public ThreadPoolProcessBuilder<T> withItems(List<T> items) {
            this.items = items;
            return this;
        }

        public ThreadPoolProcessBuilder<T> withIndex(Integer index) {
            this.index = index;
            return this;
        }

        public ThreadPoolProcessBuilder<T> withPartitionSize(Integer partitionSize) {
            this.partitionSize = partitionSize;
            return this;
        }

        public ThreadPoolProcessBuilder<T> withMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public ThreadPoolProcess<T> build() {
            ThreadPoolProcess<T> threadPoolProcess = new ThreadPoolProcess<>();
            threadPoolProcess.setItems(items);
            threadPoolProcess.setIndex(index);
            threadPoolProcess.setPartitionSize(partitionSize);
            threadPoolProcess.setMetadata(metadata);
            return threadPoolProcess;
        }
    }
}
