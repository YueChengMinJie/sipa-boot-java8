package com.sipa.boot.java8.data.mongodb.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.data.mongodb.annotation.QueryField;

/**
 * @author feizhihao
 * @version 2019-12-16 反射工具类.
 */
public class ReflectionUtils {
    private final static Log log = LogFactory.get(ReflectionUtils.class);

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型. 如无法找到, 返回Object.class. eg. public UserDao extends HibernateDao<User>
     *
     * @param clazz
     *            The class to introspect
     * @return the first generic declaration, or Object.class if cannot be determined.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Class<T> getSuperClassGenericType(final Class clazz) {
        return getSuperClassGenericType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
     * <p>
     * 如public UserDao extends HibernateDao<User,Long>
     *
     * @param clazz
     *            clazz The class to introspect
     * @param index
     *            the Index of the generic declaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be determined.
     */
    @SuppressWarnings("rawtypes")
    public static Class getSuperClassGenericType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            log.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType)genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            log.warn(
                "Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            log.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class)params[index];
    }

    /**
     * 根据对象获得mongodb Update语句 除id字段以外，所有被赋值的字段都会成为修改项
     */
    public static Update getUpdateObj(final Object obj) {
        if (obj == null) {
            return null;
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        Update update = Update.update("updateTime", LocalDateTime.now());
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    String lowerName = field.getName().toLowerCase();
                    if ("id".equals(lowerName) || "serialversionuid".equals(lowerName)) {
                        continue;
                    }
                    update = update.set(field.getName(), value);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error(e);
            }
        }
        return update;
    }

    /**
     * 查询条件 Query
     */
    public static Query getQueryObj(final Object obj) {
        Query query = new Query();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    QueryField queryField = field.getAnnotation(QueryField.class);
                    if (queryField != null) {
                        query.addCriteria(queryField.type().buildCriteria(queryField, field, value));
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error(e);
            }
        }
        return query;
    }
}
