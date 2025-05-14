package com.sipa.boot.java8.iot.core.enumerate;

import java.util.Arrays;

import com.sipa.boot.java8.iot.core.message.codec.Transports;
import com.sipa.boot.java8.iot.core.message.codec.base.ITransport;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public enum EDefaultTransport implements ITransport {
    MQTT("MQTT"),

    MQTT_TLS("MQTT TLS"),

    UDP("UDP"),

    UDP_DTLS("UDP DTLS"),

    CoAP("CoAP"),

    CoAP_DTLS("CoAP DTLS"),

    TCP("TCP"),

    TCP_TLS("TCP TLS"),

    HTTP("HTTP"),

    HTTPS("HTTPS"),

    WebSocket("WebSocket"),

    WebSockets("WebSocket TLS");

    static {
        Transports.register(Arrays.asList(EDefaultTransport.values()));
    }

    private final String name;

    EDefaultTransport(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return name();
    }
}
