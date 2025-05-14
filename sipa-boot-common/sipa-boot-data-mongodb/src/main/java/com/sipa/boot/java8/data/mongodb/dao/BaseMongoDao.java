package com.sipa.boot.java8.data.mongodb.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.sipa.boot.java8.data.mongodb.entity.Page;

/**
 * MongoDB通用Dao
 *
 * @param <T>
 * @author feizhihao
 * @version 2019-12-16
 */
public interface BaseMongoDao<T> {
    /**
     * 保存一个对象到mongodb
     */
    T save(T entity);

    /**
     * 保存一组对象到mongodb
     */
    Collection<T> saveBatch(List<T> entities);

    /**
     * 根据id找到后替换
     */
    T findAndReplace(String id, T entity, Class<T> clazz);

    /**
     * 根据id删除对象
     */
    void deleteById(T t);

    /**
     * 根据对象的属性删除
     */
    void deleteByCondition(T t);

    /**
     * 根据id进行更新
     */
    void updateById(String id, T t);

    /**
     * 根据对象的属性查询
     */
    List<T> findByCondition(T t);

    /**
     * 通过条件查询实体(集合)
     */
    List<T> find(Query query);

    /**
     * 通过一定的条件查询一个实体
     */
    T findOne(Query query);

    /**
     * 通过条件查询更新数据
     */
    void update(Query query, Update update);

    /**
     * 通过ID获取记录
     */
    T findById(String id);

    /**
     * 通过ID获取记录,并且指定了集合名(表的意思)
     */
    T findById(String id, String collectionName);

    /**
     * 通过条件查询,查询分页结果
     */
    Page<T> findPage(Page<T> page, Query query);

    /**
     * 求数据总和
     */
    long count(Query query);

    /**
     * 获取MongoDB模板操作
     */
    MongoTemplate getMongoTemplate();
}
