package com.sipa.boot.java8.common.mail.enumeration;

/**
 * @author zhouxiajie
 * @date 2020/12/14
 */
public enum EConfigFrom {
    /**
     * 0：默认值
     */
    DEFAULT(0, "默认值"),

    /**
     * 1：配置文件
     */
    CONFIG_FILE(1, "配置文件"),

    /**
     * 2：数据库
     */
    DATABASE(2, "数据库");

    private Integer code;

    private String desc;

    EConfigFrom(final Integer code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
