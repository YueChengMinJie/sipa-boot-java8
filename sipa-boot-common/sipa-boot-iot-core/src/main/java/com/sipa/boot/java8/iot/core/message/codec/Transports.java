package com.sipa.boot.java8.iot.core.message.codec;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.sipa.boot.java8.iot.core.message.codec.base.ITransport;

/**
 * @author caszhou
 * @date 2021/9/23
 */
public class Transports {
    private static final Map<String, ITransport> ALL = new ConcurrentHashMap<>();

    public static void register(Collection<ITransport> transport) {
        transport.forEach(Transports::register);
    }

    public static void register(ITransport transport) {
        ALL.put(transport.getId().toUpperCase(), transport);
    }

    public static List<ITransport> get() {
        return new ArrayList<>(ALL.values());
    }

    public static Optional<ITransport> lookup(String id) {
        return Optional.ofNullable(ALL.get(id.toUpperCase()));
    }
}
