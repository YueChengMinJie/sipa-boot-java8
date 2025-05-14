package com.sipa.boot.java8.data.mongodb.convert.ieum;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.convert.ReadingConverter;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * @author zhouxiajie
 * @date 2020/11/10
 */
public class EnumReadFactory implements ConverterFactory<Integer, Enum<?>> {
    @Override
    public <T extends Enum<?>> Converter<Integer, T> getConverter(Class<T> targetType) {
        return new EnumReadConverter(targetType);
    }

    @ReadingConverter
    public static class EnumReadConverter<T extends Enum<?>> implements Converter<Integer, Enum<?>> {
        private final Class<T> enumType;

        public EnumReadConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public Enum<?> convert(Integer source) {
            if (ArrayUtils.isNotEmpty(enumType.getEnumConstants()) && enumType.getEnumConstants()[0] instanceof IEnum) {
                for (T t : enumType.getEnumConstants()) {
                    if (t instanceof IEnum) {
                        if (Integer.parseInt(String.valueOf(((IEnum<?>)t).getValue())) == source) {
                            return t;
                        }
                    }
                }
            }
            return null;
        }
    }
}
