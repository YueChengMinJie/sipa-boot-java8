package com.sipa.boot.java8.iot.core.codec.tacitly;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class StringCodec implements ICodec<String> {
    public static StringCodec DEFAULT = of(Charset.defaultCharset());

    public static StringCodec UTF8 = of(StandardCharsets.UTF_8);

    public static StringCodec ASCII = of(StandardCharsets.US_ASCII);

    public static StringCodec of(Charset charset) {
        return new StringCodec(charset);
    }

    private final Charset charset;

    private StringCodec(Charset charset) {
        this.charset = charset;
    }

    @Override
    public Class<String> forType() {
        return String.class;
    }

    @Override
    public String decode(@Nonnull IPayload payload) {
        return payload.getBody().toString(charset);
    }

    @Override
    public IPayload encode(String body) {
        return IPayload.of(body.getBytes(charset));
    }
}
