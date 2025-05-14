package com.sipa.boot.java8.common.enums;

/**
 * @author zhouxiajie
 * @date 2018/2/5
 */
public enum EResCode {
    // 全局错误
    GLOBAL,
    // 应用返回的错误
    APP,
    // 网关超时
    FALLBACK_GATEWAY_TIMEOUT,
    // 服务错误
    FALLBACK_INTERNAL_SERVER_ERROR,
    // 认证授权
    AUTH,
    // 服务未注册
    RIBBON_UNREGISTER,
    // 重复提交
    IDEMPOTENT
}
