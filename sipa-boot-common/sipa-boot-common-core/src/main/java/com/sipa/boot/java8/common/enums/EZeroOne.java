package com.sipa.boot.java8.common.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhouxiajie
 * @date 2018/2/5
 */
@Getter
@AllArgsConstructor
public enum EZeroOne implements IEnum<Integer> {
    // 成功
    ZERO(0),

    // 应用返回的错误
    ONE(1),

    ;

    private final Integer value;
}
