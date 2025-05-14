package com.sipa.boot.java8.iot.core.message.codec.base;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import com.sipa.boot.java8.iot.core.message.codec.Transports;

/**
 * @author caszhou
 * @date 2021/9/23
 */
public interface ITransport {
    String getId();

    default String getName() {
        return getId();
    }

    default String getDescription() {
        return null;
    }

    default boolean isSame(ITransport transport) {
        return this == transport || this.getId().equals(transport.getId());
    }

    default boolean isSame(String transportId) {
        return this.getId().equals(transportId);
    }

    static ITransport of(String id) {
        return lookup(id).orElseGet(() -> (ITransport & Serializable)() -> id);
    }

    static Optional<ITransport> lookup(String id) {
        return Transports.lookup(id);
    }

    static List<ITransport> getAll() {
        return Transports.get();
    }
}
