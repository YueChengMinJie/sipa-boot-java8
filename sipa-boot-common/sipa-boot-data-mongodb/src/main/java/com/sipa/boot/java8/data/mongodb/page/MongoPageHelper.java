package com.sipa.boot.java8.data.mongodb.page;

import com.sipa.boot.java8.data.mongodb.constants.SipaBootMongoConstants;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhouxiajie
 * @date 2020/7/29
 */
public class MongoPageHelper {
    private static final int FIRST_PAGE_NUM = 1;

    private final MongoTemplate mongoTemplate;

    public MongoPageHelper(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 分页查询，直接返回集合类型的结果.
     */
    public <T> MongoPageResult<T> pageQuery(Query query, Class<T> entityClass, Integer size, Integer current) {
        return pageQuery(query, entityClass, size, current, Function.identity(), null);
    }

    /**
     * 小数据 - 分页查询，不考虑条件分页，直接使用skip-limit来分页.
     */
    public <T, R> MongoPageResult<R> pageQuery(Query query, Class<T> entityClass, Integer size, Integer current,
        Function<T, R> mapper) {
        return pageQuery(query, entityClass, size, current, mapper, null);
    }

    /**
     * 小数据 - 分页查询.
     *
     * @param query
     *            Mongo Query对象，构造你自己的查询条件.
     * @param entityClass
     *            Mongo collection定义的entity class，用来确定查询哪个集合.
     * @param mapper
     *            映射器，你从db查出来的list的元素类型是entityClass, 如果你想要转换成另一个对象，比如去掉敏感字段等，可以使用mapper来决定如何转换.
     * @param size
     *            分页的大小.
     * @param current
     *            当前页.
     * @param lastId
     *            条件分页参数, 区别于skip-limit，采用find(_id>lastId).limit分页. 如果不跳页，像朋友圈，微博这样下拉刷新的分页需求，需要传递上一页的最后一条记录的ObjectId。
     *            如果是null，则返回pageNum那一页.
     * @param <T>
     *            collection定义的class类型.
     * @param <R>
     *            最终返回时，展现给页面时的一条记录的类型。
     * @return MongodbPageResult，一个封装page信息的对象.
     */
    public <T, R> MongoPageResult<R> pageQuery(Query query, Class<T> entityClass, Integer size, Integer current,
        Function<T, R> mapper, String lastId) {
        // 分页逻辑
        long total = mongoTemplate.count(query, entityClass);
        final Integer pages = (int)Math.ceil(total / (double)size);
        if (current <= 0 || current > pages) {
            current = FIRST_PAGE_NUM;
        }
        final Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(lastId)) {
            if (current != FIRST_PAGE_NUM) {
                criteria.and(SipaBootMongoConstants.Property.ID).gt(new ObjectId(lastId));
            }
            query.limit(size);
        } else {
            int skip = size * (current - 1);
            query.skip(skip).limit(size);
        }

        final List<T> entityList =
            mongoTemplate.find(
                query.addCriteria(criteria)
                    .with(Sort.by(Collections
                        .singletonList(new Sort.Order(Sort.Direction.ASC, SipaBootMongoConstants.Property.ID)))),
                entityClass);

        final MongoPageResult<R> MongoPageResult = new MongoPageResult<>();
        MongoPageResult.setTotal((int)total);
        MongoPageResult.setPages(pages);
        MongoPageResult.setSize(size);
        MongoPageResult.setCurrent(current);
        MongoPageResult.setRecords(entityList.stream().map(mapper).collect(Collectors.toList()));
        return MongoPageResult;
    }

    /**
     * 大数据 - 分页查询.
     *
     * @param query
     *            Mongo Query对象，构造你自己的查询条件.
     * @param entityClass
     *            Mongo collection定义的entity class，用来确定查询哪个集合.
     * @param mapper
     *            映射器，你从db查出来的list的元素类型是entityClass, 如果你想要转换成另一个对象，比如去掉敏感字段等，可以使用mapper来决定如何转换.
     * @param size
     *            分页的大小.
     * @param current
     *            当前页.
     * @param lastId
     *            条件分页参数, 区别于skip-limit，采用find(_id>lastId).limit分页. 如果不跳页，像朋友圈，微博这样下拉刷新的分页需求，需要传递上一页的最后一条记录的ObjectId。
     *            如果是null，则返回pageNum那一页.
     * @param <T>
     *            collection定义的class类型.
     * @param <R>
     *            最终返回时，展现给页面时的一条记录的类型。
     * @return MongodbPageResult，一个封装page信息的对象.
     */
    public <T, R> MongoPageResult<R> bigPageQuery(Query query, Class<T> entityClass, Integer size, Integer current,
        Function<T, R> mapper, String lastId) {
        // 分页逻辑
        if (current <= 0) {
            current = FIRST_PAGE_NUM;
        }
        final Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(lastId) && current != FIRST_PAGE_NUM) {
            criteria.and(SipaBootMongoConstants.Property.ID).gt(new ObjectId(lastId));
        }
        query.limit(size);

        final List<T> entityList =
            mongoTemplate.find(
                query.addCriteria(criteria)
                    .with(Sort.by(Collections
                        .singletonList(new Sort.Order(Sort.Direction.ASC, SipaBootMongoConstants.Property.ID)))),
                entityClass);

        final MongoPageResult<R> MongoPageResult = new MongoPageResult<>();
        MongoPageResult.setSize(size);
        MongoPageResult.setCurrent(current);
        MongoPageResult.setRecords(entityList.stream().map(mapper).collect(Collectors.toList()));
        return MongoPageResult;
    }

    /**
     * 大数据索引查询 - 分页查询.
     *
     * @param query
     *            Mongo Query对象，构造你自己的查询条件.
     * @param entityClass
     *            Mongo collection定义的entity class，用来确定查询哪个集合.
     * @param mapper
     *            映射器，你从db查出来的list的元素类型是entityClass, 如果你想要转换成另一个对象，比如去掉敏感字段等，可以使用mapper来决定如何转换.
     * @param size
     *            分页的大小.
     * @param current
     *            当前页. 如果是null，则返回pageNum那一页.
     * @param <T>
     *            collection定义的class类型.
     * @param <R>
     *            最终返回时，展现给页面时的一条记录的类型。
     * @return MongodbPageResult，一个封装page信息的对象.
     */
    public <T, R> MongoPageResult<R> bigPageQueryWithIndex(Query query, Class<T> entityClass, Integer size,
        Integer current, Function<T, R> mapper) {
        // 分页逻辑
        if (current <= 0) {
            current = FIRST_PAGE_NUM;
        }
        query.limit(size);
        final List<T> entityList = mongoTemplate.find(query, entityClass);

        final MongoPageResult<R> MongoPageResult = new MongoPageResult<>();
        MongoPageResult.setSize(size);
        MongoPageResult.setCurrent(current);
        MongoPageResult.setRecords(entityList.stream().map(mapper).collect(Collectors.toList()));
        return MongoPageResult;
    }
}
