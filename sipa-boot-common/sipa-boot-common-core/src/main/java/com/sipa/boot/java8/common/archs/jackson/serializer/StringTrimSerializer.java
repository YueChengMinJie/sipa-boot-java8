package com.sipa.boot.java8.common.archs.jackson.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author zhouxiajie
 * @date 2019-01-22
 */
public class StringTrimSerializer extends JsonSerializer<String> {
    public static final StringTrimSerializer instance = new StringTrimSerializer();

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws IOException {
        if (s != null) {
            jsonGenerator.writeString(s.trim());
        }
    }
}
