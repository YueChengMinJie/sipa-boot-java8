package com.sipa.boot.java8.iot.core.device.base;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.iot.core.device.AuthenticationResponse;

import reactor.core.publisher.Mono;

/**
 * 认证器,用于设备连接的时候进行认证
 *
 * @author caszhou
 * @date 2021/10/5
 */
public interface IAuthenticator {
    /**
     * 对指定对设备进行认证
     *
     * @param request
     *            认证请求
     * @param device
     *            设备
     * @return 认证结果
     */
    Mono<AuthenticationResponse> authenticate(@Nonnull IAuthenticationRequest request, @Nonnull IDeviceOperator device);

    /**
     * 在网络连接建立的时候,可能无法获取设备的标识(如:http,websocket等),则会调用此方法来进行认证. 注意:
     * 认证通过后,需要设置设备ID.{@link AuthenticationResponse#success(String)}
     *
     * @param request
     *            认证请求
     * @param registry
     *            设备注册中心
     * @return 认证结果
     */
    default Mono<AuthenticationResponse> authenticate(@Nonnull IAuthenticationRequest request,
        @Nonnull IDeviceRegistry registry) {
        return Mono.just(AuthenticationResponse.error(500, "不支持的认证方式"));
    }
}
