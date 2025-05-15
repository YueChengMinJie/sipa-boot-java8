package com.sipa.boot.java8.data.mongodb.convert.ieum;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author zhouxiajie
 * @date 2020/11/10
 */
@WritingConverter
public class EnumWriterConverter implements Converter<Enum<?>, Object> {
    @Override
    public Object convert(Enum<?> anEnum) {
        if (anEnum instanceof IEnum) {
            return ((IEnum<?>)anEnum).getValue();
        } else {
            return anEnum.name();
        }
    }
}
