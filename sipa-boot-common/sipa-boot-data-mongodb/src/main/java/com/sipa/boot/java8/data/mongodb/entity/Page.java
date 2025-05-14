package com.sipa.boot.java8.data.mongodb.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 分页参数实体
 *
 * @param <T>
 * @author feizhihao
 * @version 2019-12-16
 */
public class Page<T> implements Serializable {
    private static final long serialVersionUID = 5760097915453738435L;

    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 每页显示个数
     */
    private int size;

    /**
     * 当前页数
     */
    private int current;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 总记录数
     */
    private int total;

    /**
     * 结果列表
     */
    private List<T> records;

    public Page() {
        this.current = 1;
        this.size = DEFAULT_PAGE_SIZE;
    }

    public Page(int current, int size) {
        this.current = current <= 0 ? 1 : current;
        this.size = size <= 0 ? 1 : size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * 设置结果 及总页数
     *
     * @param rows
     */
    public void build(List<T> rows) {
        this.setRecords(rows);
        int count = this.getTotal();
        int divisor = count / this.getSize();
        int remainder = count % this.getSize();
        this.setTotalPage(remainder == 0 ? divisor == 0 ? 1 : divisor : divisor + 1);
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
