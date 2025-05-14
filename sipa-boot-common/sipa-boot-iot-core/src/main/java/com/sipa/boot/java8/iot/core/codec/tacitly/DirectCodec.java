package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class DirectCodec implements ICodec<IPayload> {
    public static final DirectCodec INSTANCE = new DirectCodec();

    public static <T extends IPayload> ICodec<T> instance() {
        return (ICodec<T>)INSTANCE;
    }

    @Override
    public Class<IPayload> forType() {
        return IPayload.class;
    }

    @Override
    public IPayload decode(@Nonnull IPayload payload) {
        return payload;
    }

    @Override
    public IPayload encode(IPayload body) {
        return body;
    }
}
