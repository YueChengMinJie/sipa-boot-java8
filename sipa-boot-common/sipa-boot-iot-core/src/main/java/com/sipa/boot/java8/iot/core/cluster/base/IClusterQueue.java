package com.sipa.boot.java8.iot.core.cluster.base;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public interface IClusterQueue<T> {
    /**
     * 订阅队列中的数据
     *
     * @return 数据流
     */
    @Nonnull
    Flux<T> subscribe();

    /**
     * 消费队列中的数据,队列为空时返回{@link Mono#empty()}
     *
     * @return 单个数据流
     */
    @Nonnull
    Mono<T> poll();

    /**
     * 向队列中添加数据
     *
     * @param publisher
     *            数据流
     * @return 添加结果
     */
    Mono<Boolean> add(Publisher<T> publisher);

    /**
     * 向队列中批量添加数据
     *
     * @param publisher
     *            数据流
     * @return 添加结果
     */
    Mono<Boolean> addBatch(Publisher<? extends Collection<T>> publisher);

    /**
     * 设置本地消费占比,如果当前队列在同一个进程中存在消费者,则根据此因子进行本地消费.而不发送到集群.
     *
     * @param percent
     *            0-10之间的值
     */
    void setLocalConsumerPercent(float percent);

    /**
     * 停止队列,停止后不再消费队列数据.
     */
    void stop();

    /**
     * 获取队列中消息数量
     *
     * @return 消息数量
     */
    Mono<Integer> size();

    void setPollMod(EMod mod);

    enum EMod {
        // 先进先出
        FIFO,
        // 后进先出
        LIFO
    }
}
