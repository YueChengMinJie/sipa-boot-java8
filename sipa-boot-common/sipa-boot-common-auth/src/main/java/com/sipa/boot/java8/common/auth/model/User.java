package com.sipa.boot.java8.common.auth.model;

/**
 * @author zhouxiajie
 * @date 2019/10/8
 */
public class User {
    private String id;

    private Integer type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
