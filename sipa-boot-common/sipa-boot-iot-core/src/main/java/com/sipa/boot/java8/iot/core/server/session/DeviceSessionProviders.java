package com.sipa.boot.java8.iot.core.server.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSessionProvider;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class DeviceSessionProviders {
    public static final Map<String, IDeviceSessionProvider> PROVIDERS = new ConcurrentHashMap<>();

    static {
        KeepOnlineDeviceSessionProvider.load();
    }

    public static void register(IDeviceSessionProvider provider) {
        PROVIDERS.put(provider.getId(), provider);
    }

    public static Optional<IDeviceSessionProvider> lookup(String id) {
        return Optional.ofNullable(PROVIDERS.get(id));
    }
}
