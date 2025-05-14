package com.sipa.boot.java8.iot.core.enumerate;

import com.sipa.boot.java8.iot.core.config.base.IConfigKey;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public enum EDeviceConfigKey implements IConfigKey<String> {
    id("ID"),

    metadata("物模型"),

    productId("产品ID"),

    protocol("消息协议"),

    parentGatewayId("上级网关设备ID"),

    connectionServerId("当前设备连接的服务ID"),

    sessionId("设备会话ID"),

    shadow("设备影子"),

    // 遗言，用于缓存消息，等设备上线时发送指令
    will("遗言");

    String name;

    EDeviceConfigKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static IConfigKey<Boolean> isGatewayDevice = IConfigKey.of("isGatewayDevice", "是否为网关设备");

    // 通常用于子设备状态
    public static IConfigKey<Boolean> selfManageState = IConfigKey.of("selfManageState", "状态自管理");

    public static IConfigKey<Long> firstPropertyTime = IConfigKey.of("firstProperty", "首次上报属性的时间");

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
