package com.sipa.boot.java8.iot.core.server.base;

import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.sipa.boot.java8.iot.core.device.DeviceStateInfo;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessageReply;
import com.sipa.boot.java8.iot.core.message.base.IMessage;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 消息处理器,在服务启动后,用于接收来着平台的指令并进行相应的处理
 *
 * @author caszhou
 * @date 2021/10/3
 */
public interface IMessageHandler {
    /**
     * 监听发往设备的指令
     *
     * @param serverId
     *            服务ID,在集群时,不同的节点serverId不同
     * @return 发网设备的消息指令流
     */
    Flux<IMessage> handleSendToDeviceMessage(String serverId);

    /**
     * 监听获取设备真实状态请求,并响应状态结果
     *
     * @param serverId
     *            服务ID,在集群时,不同的节点serverId不同
     * @param stateMapper
     *            状态检查器
     * @return
     */
    Disposable handleGetDeviceState(String serverId, Function<Publisher<String>, Flux<DeviceStateInfo>> stateMapper);

    /**
     * 回复平台下发的指令
     *
     * @param message
     *            回复指令
     * @return success
     */
    Mono<Boolean> reply(IDeviceMessageReply message);
}
