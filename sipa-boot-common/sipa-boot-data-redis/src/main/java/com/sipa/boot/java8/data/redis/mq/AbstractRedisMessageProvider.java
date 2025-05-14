package com.sipa.boot.java8.data.redis.mq;

/**
 * @author feizhihao
 * @date 2019-06-14 14:40
 */
public abstract class AbstractRedisMessageProvider {
    public void beforePush(Object object) {}

    public void doPush(String topic, Object object) {}

    /**
     * 生产消息.
     *
     * @param topic
     *            主题
     * @param object
     *            消息
     */
    public void push(String topic, Object object) {
        this.beforePush(object);
        this.doPush(topic, object);
    }
}
