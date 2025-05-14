package com.sipa.boot.java8.data.mongodb.base;

/**
 * @author zhouxiajie
 * @date 2020/8/6
 */
public abstract class MongodbBaseRequest {
    private Integer size;

    private Integer current;

    private String lastId;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }
}
