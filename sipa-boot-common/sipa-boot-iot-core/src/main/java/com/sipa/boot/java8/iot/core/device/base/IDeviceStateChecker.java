package com.sipa.boot.java8.iot.core.device.base;

import javax.validation.constraints.NotNull;

import com.sipa.boot.java8.iot.core.constant.IDeviceState;

import reactor.core.publisher.Mono;

/**
 * 设备状态检查器,用于自定义设备状态检查
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IDeviceStateChecker {
    /**
     * 检查设备状态
     *
     * @param device
     *            设备操作接口
     * @return 设备状态 {@link IDeviceState}
     */
    @NotNull
    Mono<Byte> checkState(@NotNull IDeviceOperator device);

    /**
     * 排序需要，值越小优先级越高
     *
     * @return 序号
     */
    default long order() {
        return Long.MAX_VALUE;
    }
}
