package com.sipa.boot.java8.data.mongodb.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.google.common.collect.Lists;
import com.sipa.boot.java8.data.mongodb.entity.Page;
import com.sipa.boot.java8.data.mongodb.utils.ReflectionUtils;

/**
 * MongoDB通用Dao抽象实现
 *
 * @param <T>
 * @author feizhihao
 * @version 2019-12-16
 */
public abstract class MongoDaoSupport<T> implements BaseMongoDao<T> {
    @Resource
    @Qualifier("mongoTemplate")
    protected MongoTemplate mongoTemplate;

    /**
     * 获取MongoDB模板操作
     *
     * @return MongoTemplate
     */
    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    /**
     * 保存一个对象到mongodb
     */
    @Override
    public T save(T t) {
        mongoTemplate.save(t);
        return t;
    }

    @Override
    public Collection<T> saveBatch(List<T> entities) {
        Collection<T> collection = Lists.newArrayList();

        List<List<T>> batchList = Lists.partition(entities, 1000);
        if (CollectionUtils.isNotEmpty(batchList)) {
            for (List<T> ts : batchList) {
                collection.addAll(mongoTemplate.insertAll(ts));
            }
        }

        return collection;
    }

    /**
     * 根据id找到后替换
     */
    @Override
    public T findAndReplace(String id, T t, Class<T> clazz) {
        Optional<T> result = mongoTemplate.update(clazz)
            .matching(query(where("_id").is(id)))
            .replaceWith(t)
            .withOptions(FindAndReplaceOptions.options().upsert())
            .as(clazz)
            .findAndReplace();
        return result.orElse(null);
    }

    /**
     * 根据id删除对象
     */
    @Override
    public void deleteById(T t) {
        mongoTemplate.remove(t);
    }

    /**
     * 根据对象的属性删除
     */
    @Override
    public void deleteByCondition(T t) {
        Query query = buildBaseQuery(t);
        mongoTemplate.remove(query, getEntityClass());
    }

    /**
     * 根据id进行更新
     */
    @Override
    public void updateById(String id, T t) {
        Query query = new Query();
        query.addCriteria(where("id").is(id));
        Update update = buildBaseUpdate(t);
        update(query, update);
    }

    /**
     * 根据对象的属性查询
     */
    @Override
    public List<T> findByCondition(T t) {
        Query query = buildBaseQuery(t);
        return mongoTemplate.find(query, getEntityClass());
    }

    /**
     * 通过条件查询实体(集合)
     */
    @Override
    public List<T> find(Query query) {
        return mongoTemplate.find(query, this.getEntityClass());
    }

    /**
     * 通过一定的条件查询一个实体
     */
    @Override
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, this.getEntityClass());
    }

    /**
     * 通过ID获取记录,并且指定了集合名(表的意思)
     */
    public T findOne(Query query, String collectionName) {
        return mongoTemplate.findOne(query, this.getEntityClass(), collectionName);
    }

    /**
     * 通过条件查询更新数据
     */
    @Override
    public void update(Query query, Update update) {
        mongoTemplate.updateMulti(query, update, this.getEntityClass());
    }

    /**
     * 通过ID获取记录
     */
    @Override
    public T findById(String id) {
        return mongoTemplate.findById(id, this.getEntityClass());
    }

    /**
     * 通过ID获取记录,并且指定了集合名(表的意思)
     */
    @Override
    public T findById(String id, String collectionName) {
        return mongoTemplate.findById(id, this.getEntityClass(), collectionName);
    }

    /**
     * 通过条件查询,查询分页结果
     */
    @Override
    public Page<T> findPage(Page<T> page, Query query) {
        // 如果没有条件 则所有全部
        query = query == null ? new Query(where("_id").exists(true)) : query;
        long count = this.count(query);
        // 总数
        page.setTotal((int)count);
        int currentPage = page.getCurrent();
        int pageSize = page.getSize();
        query.skip((currentPage - 1) * pageSize).limit(pageSize);
        List<T> rows = this.find(query);
        page.build(rows);
        return page;
    }

    /**
     * 求数据总和
     */
    @Override
    public long count(Query query) {
        return mongoTemplate.count(query, this.getEntityClass());
    }

    /**
     * 根据vo构建查询条件Query
     */
    private Query buildBaseQuery(T t) {
        return ReflectionUtils.getQueryObj(t);
    }

    /**
     * 根据vo构建更新条件Query
     */
    private Update buildBaseUpdate(T t) {
        return ReflectionUtils.getUpdateObj(t);
    }

    /**
     * 获取需要操作的实体类class
     */
    protected Class<T> getEntityClass() {
        return ReflectionUtils.getSuperClassGenericType(getClass());
    }
}
