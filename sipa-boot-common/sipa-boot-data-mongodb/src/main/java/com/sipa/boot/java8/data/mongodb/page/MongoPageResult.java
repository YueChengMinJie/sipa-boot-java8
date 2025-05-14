package com.sipa.boot.java8.data.mongodb.page;

import java.util.List;

/**
 * @author zhouxiajie
 * @date 2020/7/29
 */
public class MongoPageResult<T> {
    /**
     * 页码，从1开始
     */
    private Integer current;

    /**
     * 页面大小
     */
    private Integer size;

    /**
     * 总数
     */
    private Integer total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 数据
     */
    private List<T> records;

    public MongoPageResult() {}

    public MongoPageResult(Integer current, Integer size, Integer total, Integer pages, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.pages = pages;
        this.records = records;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
