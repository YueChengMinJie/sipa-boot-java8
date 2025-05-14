package com.sipa.boot.java8.common.oauth2.enumerate;

/**
 * @author zhouxiajie
 * @date 2019-05-30
 */
public enum EVerifyType {
    /**
     * 普通认证
     */
    NORMAL("normal", "短信"),

    /**
     * 短信验证码认证
     */
    SMS("sms", "短信验证码认证");

    private final String code;

    private final String desc;

    EVerifyType(final String code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static EVerifyType of(String scope) {
        for (EVerifyType eUserScope : EVerifyType.values()) {
            if (eUserScope.getCode().equals(scope)) {
                return eUserScope;
            }
        }
        return NORMAL;
    }
}
