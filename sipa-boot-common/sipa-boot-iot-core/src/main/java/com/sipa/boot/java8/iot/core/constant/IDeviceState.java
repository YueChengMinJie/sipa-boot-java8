package com.sipa.boot.java8.iot.core.constant;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public interface IDeviceState {
    /**
     * 未知
     */
    byte unknown = 0;

    /**
     * 在线
     */
    byte online = 1;

    /**
     * 未激活
     */
    byte noActive = -3;

    /**
     * 离线
     */
    byte offline = -1;

    /**
     * 检查状态超时
     */
    byte timeout = -2;
}
