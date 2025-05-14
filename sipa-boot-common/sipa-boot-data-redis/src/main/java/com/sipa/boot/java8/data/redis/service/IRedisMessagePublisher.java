package com.sipa.boot.java8.data.redis.service;

/**
 * @author zhouxiajie
 * @date 2019-03-16
 */
public interface IRedisMessagePublisher {
    /**
     * 发布.
     *
     * @param message
     *            消息
     */
    void publish(String message);

    /**
     * 发布.
     *
     * @param topic
     *            主题
     * @param message
     *            消息
     */
    void publish(String topic, String message);
}
