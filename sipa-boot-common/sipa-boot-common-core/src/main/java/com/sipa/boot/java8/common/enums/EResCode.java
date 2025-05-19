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
public enum EResCode implements IEnum<Integer> {
    // 全局错误
    GLOBAL(-1),

    // 成功
    SUCCESS(0),

    // 应用返回的错误
    APP(1),

    // 网关超时
    FALLBACK_GATEWAY_TIMEOUT(2),

    // 服务错误
    FALLBACK_INTERNAL_SERVER_ERROR(3),

    // 认证授权
    AUTH(4),

    // 服务未注册
    RIBBON_UNREGISTER(4),

    // 重复提交
    IDEMPOTENT(5),

    ;

    private final Integer value;
}
