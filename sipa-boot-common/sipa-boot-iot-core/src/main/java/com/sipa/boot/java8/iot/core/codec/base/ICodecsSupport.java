package com.sipa.boot.java8.iot.core.codec.base;

import java.util.Optional;

import org.springframework.core.ResolvableType;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public interface ICodecsSupport {
    <T> Optional<ICodec<T>> lookup(ResolvableType type);

    int getOrder();
}
