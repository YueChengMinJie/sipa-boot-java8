package com.sipa.boot.java8.iot.core.device.base;

import java.time.Duration;
import java.util.Collection;

import org.reactivestreams.Publisher;

import com.sipa.boot.java8.iot.core.device.DeviceStateInfo;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessageReply;
import com.sipa.boot.java8.iot.core.message.base.IMessage;
import com.sipa.boot.java8.iot.core.message.broadcast.base.IBroadcastMessage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public interface IDeviceOperationBroker {
    /**
     * 获取指定服务里设备状态
     *
     * @param deviceGatewayServerId
     *            设备所在服务ID
     * @param deviceIdList
     *            设备列表
     * @return 设备状态
     */
    Flux<DeviceStateInfo> getDeviceState(String deviceGatewayServerId, Collection<String> deviceIdList);

    /**
     * 根据消息ID监听响应
     *
     * @param deviceId
     *            设备ID
     * @param messageId
     *            消息ID
     * @param timeout
     *            超时时间
     * @return 消息返回
     */
    Flux<IDeviceMessageReply> handleReply(String deviceId, String messageId, Duration timeout);

    /**
     * 发送设备消息到指定到服务
     *
     * @param deviceGatewayServerId
     *            设备所在服务ID
     * @return 有多少服务收到了此消息
     */
    Mono<Integer> send(String deviceGatewayServerId, Publisher<? extends IMessage> message);

    /**
     * 发送广播消息
     *
     * @param message
     *            广播消息
     * @return 有多少服务收到了此消息
     */
    Mono<Integer> send(Publisher<? extends IBroadcastMessage> message);
}
