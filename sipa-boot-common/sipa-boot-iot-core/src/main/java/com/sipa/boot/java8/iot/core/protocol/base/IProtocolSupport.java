package com.sipa.boot.java8.iot.core.protocol.base;

import java.util.Map;

import javax.annotation.Nonnull;

import org.springframework.core.Ordered;

import com.sipa.boot.java8.iot.core.device.AuthenticationResponse;
import com.sipa.boot.java8.iot.core.device.base.*;
import com.sipa.boot.java8.iot.core.enumerate.EDeviceMetadataType;
import com.sipa.boot.java8.iot.core.message.codec.base.IDeviceMessageCodec;
import com.sipa.boot.java8.iot.core.message.codec.base.ITransport;
import com.sipa.boot.java8.iot.core.message.interceptor.base.IDeviceMessageSenderInterceptor;
import com.sipa.boot.java8.iot.core.metadata.base.IConfigMetadata;
import com.sipa.boot.java8.iot.core.metadata.base.IDeviceMetadata;
import com.sipa.boot.java8.iot.core.metadata.base.IDeviceMetadataCodec;
import com.sipa.boot.java8.iot.core.metadata.base.IFeature;
import com.sipa.boot.java8.iot.core.server.base.IClientConnection;
import com.sipa.boot.java8.iot.core.server.base.IDeviceGatewayContext;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 消息协议支持接口，通过实现此接口来自定义消息协议
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IProtocolSupport extends Disposable, Ordered, Comparable<IProtocolSupport> {
    /**
     * 协议ID
     */
    @Nonnull
    String getId();

    /**
     * 协议名称
     */
    String getName();

    /**
     * 说明
     */
    String getDescription();

    /**
     * 版本号
     */
    @Nonnull
    String getVersion();

    /**
     * 获取支持的协议类型
     */
    Flux<? extends ITransport> getSupportedTransport();

    /**
     * 获取设备消息编码解码器
     * <ul>
     * <li>用于将平台统一的消息对象转码为设备的消息</li>
     * <li>用于将设备发送的消息转吗为平台统一的消息对象</li>
     * </ul>
     *
     * @return 消息编解码器
     */
    @Nonnull
    Mono<? extends IDeviceMessageCodec> getMessageCodec(ITransport transport);

    /**
     * 获取设备消息发送拦截器, 用于拦截发送消息的行为.
     *
     * @return 监听器
     */
    default Mono<IDeviceMessageSenderInterceptor> getSenderInterceptor() {
        return Mono.just(IDeviceMessageSenderInterceptor.DO_NOTING);
    }

    /**
     * 获取默认的设备物模型编解码器
     * <ul>
     * <li>用于将平台统一的设备定义规范转码为协议的规范</li>
     * <li>用于将协议的规范转为平台统一的设备定义规范</li>
     * </ul>
     *
     * @return 物模型编解码器
     */
    @Nonnull
    IDeviceMetadataCodec getMetadataCodec();

    /**
     * 获取所有支持的物模型编解码器
     *
     * @return 物模型
     */
    default Flux<IDeviceMetadataCodec> getMetadataCodecs() {
        return Flux.just(getMetadataCodec());
    }

    /**
     * 获取自定义设备状态检查器,用于检查设备状态.
     *
     * @return 设备状态检查器
     */
    @Nonnull
    default Mono<IDeviceStateChecker> getStateChecker() {
        return Mono.empty();
    }

    /**
     * 获取协议所需的配置信息定义
     *
     * @return 配置定义
     */
    default Mono<IConfigMetadata> getConfigMetadata(ITransport transport) {
        return Mono.empty();
    }

    /**
     * 获取协议初始化所需要的配置定义
     *
     * @return 配置定义
     */
    default Mono<IConfigMetadata> getInitConfigMetadata() {
        return Mono.empty();
    }

    /**
     * 获取默认物模型
     *
     * @param transport
     *            传输协议
     * @return 物模型信息
     */
    default Mono<IDeviceMetadata> getDefaultMetadata(ITransport transport) {
        return Mono.empty();
    }

    /**
     * 获取物模型拓展配置定义
     *
     * @param transport
     *            传输协议类型
     * @param metadataType
     *            物模型类型
     * @param dataTypeId
     *            数据类型ID
     * @param metadataId
     *            物模型标识
     * @return 配置定义
     */
    default Flux<IConfigMetadata> getMetadataExpandsConfig(ITransport transport, EDeviceMetadataType metadataType,
        String metadataId, String dataTypeId) {
        return Flux.empty();
    }

    /**
     * 获取协议支持的某些自定义特性
     *
     * @return 特性集
     */
    default Flux<IFeature> getFeatures(ITransport transport) {
        return Flux.empty();
    }

    @Override
    default int getOrder() {
        return Integer.MAX_VALUE;
    }

    /**
     * 当设备注册生效后调用
     *
     * @param operator
     *            设备操作接口
     * @return void
     */
    default Mono<Void> onDeviceRegister(IDeviceOperator operator) {
        return Mono.empty();
    }

    /**
     * 当设备注销前调用
     *
     * @param operator
     *            设备操作接口
     * @return void
     */
    default Mono<Void> onDeviceUnRegister(IDeviceOperator operator) {
        return Mono.empty();
    }

    /**
     * 当设备物模型变更时调用
     *
     * @param operator
     *            设备操作接口
     * @return void
     */
    default Mono<Void> onDeviceMetadataChanged(IDeviceOperator operator) {
        return Mono.empty();
    }

    /**
     * 当产品注册后调用
     *
     * @param operator
     *            产品操作接口
     * @return void
     */
    default Mono<Void> onProductRegister(IDeviceProductOperator operator) {
        return Mono.empty();
    }

    /**
     * 当产品注销前调用
     *
     * @param operator
     *            产品操作接口
     * @return void
     */
    default Mono<Void> onProductUnRegister(IDeviceProductOperator operator) {
        return Mono.empty();
    }

    /**
     * 当产品物模型变更时调用
     *
     * @param operator
     *            产品操作接口
     * @return void
     */
    default Mono<Void> onProductMetadataChanged(IDeviceProductOperator operator) {
        return Mono.empty();
    }

    /**
     * 客户端创建连接时调用,返回设备ID,表示此设备上线.
     *
     * @param transport
     *            传输协议
     * @param connection
     *            客户端连接
     * @return void
     */
    default Mono<Void> onClientConnect(ITransport transport, IClientConnection connection,
        IDeviceGatewayContext context) {
        return Mono.empty();
    }

    /**
     * 触发手动绑定子设备到网关设备
     *
     * @param gateway
     *            网关
     * @param child
     *            子设备流
     * @return void
     */
    default Mono<Void> onChildBind(IDeviceOperator gateway, Flux<IDeviceOperator> child) {
        return Mono.empty();
    }

    /**
     * 触发手动接触绑定子设备到网关设备
     *
     * @param gateway
     *            网关
     * @param child
     *            子设备流
     * @return void
     */
    default Mono<Void> onChildUnbind(IDeviceOperator gateway, Flux<IDeviceOperator> child) {
        return Mono.empty();
    }

    /**
     * 进行设备认证
     *
     * @param request
     *            认证请求，不同的连接方式实现不同
     * @param deviceOperation
     *            设备操作接口,可用于配置设备
     * @return 认证结果
     */
    @Nonnull
    Mono<AuthenticationResponse> authenticate(@Nonnull IAuthenticationRequest request,
        @Nonnull IDeviceOperator deviceOperation);

    /**
     * 对不明确的设备进行认证
     *
     * @param request
     *            认证请求
     * @param registry
     *            注册中心
     * @return 认证结果
     */
    @Nonnull
    default Mono<AuthenticationResponse> authenticate(@Nonnull IAuthenticationRequest request,
        @Nonnull IDeviceRegistry registry) {
        return Mono.error(new UnsupportedOperationException());
    }

    /**
     * 初始化协议
     *
     * @param configuration
     *            配置信息
     */
    default void init(Map<String, Object> configuration) {
    }

    /**
     * 销毁协议
     */
    @Override
    default void dispose() {
    }

    @Override
    default int compareTo(IProtocolSupport o) {
        return Integer.compare(this.getOrder(), o == null ? 0 : o.getOrder());
    }
}
