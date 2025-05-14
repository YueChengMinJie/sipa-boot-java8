package com.sipa.boot.java8.iot.core.server.session.base;

/**
 * 可替换的设备会话
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IReplaceableDeviceSession {
    void replaceWith(IDeviceSession session);
}
