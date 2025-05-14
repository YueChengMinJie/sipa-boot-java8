package com.sipa.boot.java8.common.archs.threadpool.pojo;

import java.util.Map;

/**
 * @author zhouxiajie
 * @date 2020/6/3
 */
public class ThreadPoolResult<R> {
    private R data;

    private Map<String, String> metadata;

    public R getData() {
        return data;
    }

    public void setData(R data) {
        this.data = data;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public static final class ThreadPoolResultBuilder<R> {
        private R data;

        private Map<String, String> metadata;

        private ThreadPoolResultBuilder() {}

        public static <R> ThreadPoolResultBuilder<R> aThreadPoolResult() {
            return new ThreadPoolResultBuilder<>();
        }

        public ThreadPoolResultBuilder<R> withData(R data) {
            this.data = data;
            return this;
        }

        public ThreadPoolResultBuilder<R> withMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public ThreadPoolResult<R> build() {
            ThreadPoolResult<R> threadPoolResult = new ThreadPoolResult<>();
            threadPoolResult.setData(data);
            threadPoolResult.setMetadata(metadata);
            return threadPoolResult;
        }
    }
}
