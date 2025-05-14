package com.sipa.boot.java8.iot.core.server.session.base;

/**
 * 支持持久化的Session
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IPersistentSession extends IDeviceSession {
    /**
     * @return 会话提供者
     */
    String getProvider();
}
