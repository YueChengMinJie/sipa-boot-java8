package com.sipa.boot.java8.iot.core.message.base;

import java.util.concurrent.TimeUnit;

import com.sipa.boot.java8.iot.core.message.status.DeviceOnlineMessage;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IHeaders {
    /**
     * 强制执行
     */
    IHeaderKey<Boolean> force = IHeaderKey.of("force", true, Boolean.class);

    /**
     * 保持在线,与{@link DeviceOnlineMessage}配合使用.
     */
    IHeaderKey<Boolean> keepOnline = IHeaderKey.of("keepOnline", true, Boolean.class);

    /**
     * 保持在线超时时间,超过指定时间未收到消息则认为离线
     */
    IHeaderKey<Integer> keepOnlineTimeoutSeconds = IHeaderKey.of("keepOnlineTimeoutSeconds", 600, Integer.class);

    /**
     * 异步消息,当发往设备的消息标记了为异步时,设备网关服务发送消息到设备后将立即回复到发送端
     */
    IHeaderKey<Boolean> async = IHeaderKey.of("async", false, Boolean.class);

    /**
     * 客户端地址,通常为设备IP地址
     */
    IHeaderKey<String> clientAddress = IHeaderKey.of("cliAddr", "/", String.class);

    /**
     * 发送既不管
     */
    IHeaderKey<Boolean> sendAndForget = IHeaderKey.of("sendAndForget", false);

    /**
     * 指定发送消息的超时时间
     */
    IHeaderKey<Long> timeout = IHeaderKey.of("timeout", TimeUnit.SECONDS.toMillis(10), Long.class);

    /**
     * 是否合并历史属性数据,设置此消息头后,将会把历史最新的消息合并到消息体里
     */
    IHeaderKey<Boolean> mergeLatest = IHeaderKey.of("mergeLatest", false, Boolean.class);

    /**
     * 是否为转发到父设备的消息
     */
    IHeaderKey<Boolean> dispatchToParent = IHeaderKey.of("dispatchToParent", false, Boolean.class);

    // ******** 分片消息,一个请求,设备将结果分片返回,通常用于处理大消息. **********
    /**
     * 分片消息ID(为平台下发消息时的消息ID)
     */
    IHeaderKey<String> fragmentBodyMessageId = IHeaderKey.of("frag_msg_id", null, String.class);

    /**
     * 分片数量
     */
    IHeaderKey<Integer> fragmentNumber = IHeaderKey.of("frag_num", 0, Integer.class);

    /**
     * 是否为最后一个分配,如果分片数量不确定则使用这个来表示分片结束了.
     */
    IHeaderKey<Boolean> fragmentLast = IHeaderKey.of("frag_last", false, Boolean.class);

    /**
     * 当前分片
     */
    IHeaderKey<Integer> fragmentPart = IHeaderKey.of("frag_part", 0, Integer.class);

    /**
     * 集群间消息传递标记
     */
    IHeaderKey<String> sendFrom = IHeaderKey.of("send-from", null, String.class);

    IHeaderKey<String> replyFrom = IHeaderKey.of("reply-from", null, String.class);

    /**
     * 是否使用时间戳作为数据ID
     */
    IHeaderKey<Boolean> useTimestampAsId = IHeaderKey.of("useTimestampId", false, Boolean.class);

    /**
     * 是否属性为部分属性,如果为true,在列式存储策略下,将会把之前上报的属性合并到一起进行存储.
     */
    IHeaderKey<Boolean> partialProperties = IHeaderKey.of("partialProperties", false, Boolean.class);

    /**
     * 是否开启追踪,开启后header中将添加各个操作的时间戳
     */
    IHeaderKey<Boolean> enableTrace =
        IHeaderKey.of("_trace", Boolean.getBoolean("device.message.trace.enabled"), Boolean.class);

    /**
     * 标记数据不存储
     */
    IHeaderKey<Boolean> ignoreStorage = IHeaderKey.of("ignoreStorage", false, Boolean.class);

    /**
     * 忽略记录日志
     */
    IHeaderKey<Boolean> ignoreLog = IHeaderKey.of("ignoreLog", false, Boolean.class);

    /**
     * 忽略某些操作,具体由不同的消息决定
     */
    IHeaderKey<Boolean> ignore = IHeaderKey.of("ignore", false, Boolean.class);

    /**
     * 忽略会话创建,如果设备未在线,默认为创建会话,设置此header为true后则不会自动创建会话.
     */
    IHeaderKey<Boolean> ignoreSession = IHeaderKey.of("ignoreSession", false, Boolean.class);

    /**
     * 产品ID
     */
    IHeaderKey<String> productId = IHeaderKey.of("productId", null, String.class);
}
