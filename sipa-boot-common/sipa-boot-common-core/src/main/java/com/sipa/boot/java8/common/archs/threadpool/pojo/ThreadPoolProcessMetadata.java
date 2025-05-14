package com.sipa.boot.java8.common.archs.threadpool.pojo;

import java.util.Map;

/**
 * @author zhouxiajie
 * @date 2020/6/3
 */
public class ThreadPoolProcessMetadata {
    private Map<String, Object> data;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public static final class ThreadPoolProcessMetadataBuilder {
        private Map<String, Object> data;

        private ThreadPoolProcessMetadataBuilder() {}

        public static ThreadPoolProcessMetadataBuilder aThreadPoolProcessMetadata() {
            return new ThreadPoolProcessMetadataBuilder();
        }

        public ThreadPoolProcessMetadataBuilder withData(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public ThreadPoolProcessMetadata build() {
            ThreadPoolProcessMetadata threadPoolProcessMetadata = new ThreadPoolProcessMetadata();
            threadPoolProcessMetadata.setData(data);
            return threadPoolProcessMetadata;
        }
    }
}
