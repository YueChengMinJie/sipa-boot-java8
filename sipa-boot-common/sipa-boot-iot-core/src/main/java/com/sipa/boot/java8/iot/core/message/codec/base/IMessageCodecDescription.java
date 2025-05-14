package com.sipa.boot.java8.iot.core.message.codec.base;

import javax.annotation.Nullable;

import com.sipa.boot.java8.iot.core.metadata.base.IConfigMetadata;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IMessageCodecDescription {
    String getDescription();

    @Nullable
    IConfigMetadata getConfigMetadata();
}
