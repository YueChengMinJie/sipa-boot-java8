package com.sipa.boot.java8.common.archs.jackson.serializer;

import java.io.IOException;
import java.io.Serializable;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author zhouxiajie
 * @date 2019/8/28
 */
public class EnumSerializer extends JsonSerializer<Enum> {
    public static final EnumSerializer instance = new EnumSerializer();

    @Override
    public void serialize(Enum e, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (e != null && IEnum.class.isAssignableFrom(e.getClass())) {
            IEnum ie = (IEnum)e;
            Serializable value = ie.getValue();
            if (value instanceof Integer) {
                jsonGenerator.writeNumber((Integer)value);
            } else if (value instanceof String) {
                jsonGenerator.writeString((String)value);
            } else {
                jsonGenerator.writeString(String.valueOf(value));
            }
        } else if (e != null) {
            jsonGenerator.writeNumber(e.ordinal());
        }
    }
}
