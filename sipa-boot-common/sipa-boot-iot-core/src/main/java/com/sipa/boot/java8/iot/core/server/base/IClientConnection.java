package com.sipa.boot.java8.iot.core.server.base;

import java.net.InetSocketAddress;

import com.sipa.boot.java8.iot.core.message.codec.base.IEncodedMessage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IClientConnection {
    /**
     * @return 客户端地址
     */
    InetSocketAddress address();

    /**
     * 发送消息给客户端
     *
     * @param message
     *            消息
     * @return void
     */
    Mono<Void> sendMessage(IEncodedMessage message);

    /**
     * 接收来自客户端消息
     *
     * @return 消息流
     */
    Flux<IEncodedMessage> receiveMessage();

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 连接是否还存活
     */
    boolean isAlive();
}
