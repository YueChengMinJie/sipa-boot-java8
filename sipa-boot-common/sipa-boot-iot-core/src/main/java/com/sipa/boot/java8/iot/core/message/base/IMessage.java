package com.sipa.boot.java8.iot.core.message.base;

import static com.sipa.boot.java8.iot.core.enumerate.EMessageType.UNKNOWN;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.metadata.base.IJsonable;

/**
 * @author caszhou
 * @date 2021/9/23
 */
public interface IMessage extends IJsonable, Serializable {
    default EMessageType getMessageType() {
        return UNKNOWN;
    }

    String getMessageId();

    long getTimestamp();

    Map<String, Object> getHeaders();

    IMessage addHeader(String header, Object value);

    IMessage addHeaderIfAbsent(String header, Object value);

    IMessage removeHeader(String header);

    default <T> IMessage addHeader(IHeaderKey<T> header, T value) {
        return addHeader(header.getKey(), value);
    }

    default <T> IMessage addHeaderIfAbsent(IHeaderKey<T> header, T value) {
        return addHeaderIfAbsent(header.getKey(), value);
    }

    default <T> Optional<T> getHeader(IHeaderKey<T> key) {
        return getHeader(key.getKey()).map(v -> TypeUtils.cast(v, key.getType(), ParserConfig.global));
    }

    default <T> T getHeaderOrDefault(IHeaderKey<T> key) {
        return getHeader(key).orElseGet(key::getDefaultValue);
    }

    default Optional<Object> getHeader(String header) {
        return Optional.ofNullable(getHeaders()).map(headers -> headers.get(header));
    }

    default void validate() {
    }
}
