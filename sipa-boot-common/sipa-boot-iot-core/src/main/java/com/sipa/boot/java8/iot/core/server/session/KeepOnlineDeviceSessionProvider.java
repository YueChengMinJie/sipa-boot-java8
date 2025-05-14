package com.sipa.boot.java8.iot.core.server.session;

import java.time.Duration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.device.base.IDeviceRegistry;
import com.sipa.boot.java8.iot.core.message.codec.base.ITransport;
import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSessionProvider;
import com.sipa.boot.java8.iot.core.server.session.base.IPersistentSession;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/9/28
 */
class KeepOnlineDeviceSessionProvider implements IDeviceSessionProvider {
    static final String ID = "keep_online";

    static final KeepOnlineDeviceSessionProvider INSTANCE = new KeepOnlineDeviceSessionProvider();

    static {
        DeviceSessionProviders.register(INSTANCE);
    }

    public static void load() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Mono<IPersistentSession> deserialize(byte[] sessionData, IDeviceRegistry registry) {
        JSONObject data = JSON.parseObject(sessionData, JSONObject.class);
        String deviceId = data.getString("deviceId");
        return registry.getDevice(deviceId).map(device -> {
            String id = data.getString("id");
            String transport = data.getString("transport");
            long timeout = data.getLongValue("timeout");
            long lstTime = data.getLongValue("lstTime");
            KeepOnlineSession session = new KeepOnlineSession(
                new LostDeviceSession(id, device, ITransport.of(transport)), Duration.ofMillis(timeout));
            session.setLastKeepAliveTime(lstTime);
            return session;
        });
    }

    @Override
    public Mono<byte[]> serialize(IPersistentSession session, IDeviceRegistry registry) {
        JSONObject data = new JSONObject();
        data.put("id", session.getId());
        data.put("deviceId", session.getDeviceId());
        data.put("timeout", session.getKeepAliveTimeout().toMillis());
        data.put("lstTime", session.lastPingTime());
        data.put("transport", session.getTransport().getId());
        return Mono.just(JSON.toJSONBytes(data));
    }
}
