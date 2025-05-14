package com.sipa.boot.java8.common.common.feign.convert;

import org.springframework.core.convert.converter.Converter;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * @author zhouxiajie
 * @date 2019-02-03
 */
public class UniversalReversedEnumConverter implements Converter<IEnum, String> {
    @Override
    public String convert(IEnum source) {
        return String.valueOf(source.getValue());
    }
}
