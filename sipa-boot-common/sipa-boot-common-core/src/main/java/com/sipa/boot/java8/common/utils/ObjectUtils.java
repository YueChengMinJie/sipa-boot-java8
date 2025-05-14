package com.sipa.boot.java8.common.utils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhouxiajie
 * @date 2019-02-21
 */
public class ObjectUtils {
    private static final String JAVA_PATH_NAME = "java.";

    private static final String JAVA_DATE_PATH_NAME = "java.util.Date";

    /**
     * 获取利用反射获取类里面的值和名称
     */
    public static Map<String, String> objectToStrMap(Object obj) throws IllegalAccessException {
        Map<String, String> map = new HashMap<>(16);
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(obj);
            if (null != value) {
                map.put(fieldName, String.valueOf(value));
            }
        }
        return map;
    }

    /**
     * 利用递归调用将Object中的值全部进行获取
     */
    public static Map<String, String> objectToMapString(String timeFormatStr, Object obj, String... excludeFields)
        throws IllegalAccessException {
        Map<String, String> map = new HashMap<>();

        if (excludeFields.length != 0) {
            List<String> list = Arrays.asList(excludeFields);
            objectTransfer(timeFormatStr, obj, map, list);
        } else {
            objectTransfer(timeFormatStr, obj, map, null);
        }
        return map;
    }

    /**
     * 递归调用函数
     */
    private static Map<String, String> objectTransfer(String timeFormatStr, Object obj, Map<String, String> map,
        List<String> excludeFields) throws IllegalAccessException {
        boolean isExclude = false;
        // 默认字符串
        String formatStr = "YYYY-MM-dd HH:mm:ss";
        // 设置格式化字符串
        if (timeFormatStr != null && !timeFormatStr.isEmpty()) {
            formatStr = timeFormatStr;
        }
        if (excludeFields != null) {
            isExclude = true;
        }
        Class<?> clazz = obj.getClass();
        // 获取值
        for (Field field : clazz.getDeclaredFields()) {
            String fieldName = clazz.getSimpleName() + "." + field.getName();
            // 判断是不是需要跳过某个属性
            if (isExclude && excludeFields.contains(fieldName)) {
                continue;
            }
            // 设置属性可以被访问
            field.setAccessible(true);
            Object value = field.get(obj);
            Class<?> valueClass = value.getClass();
            if (valueClass.isPrimitive()) {
                map.put(fieldName, value.toString());
            } else if (valueClass.getName().contains(JAVA_PATH_NAME)) {
                if (valueClass.getName().equals(JAVA_DATE_PATH_NAME)) {
                    // 格式化Date类型
                    SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
                    Date date = (Date)value;
                    String dataStr = sdf.format(date);
                    map.put(fieldName, dataStr);
                } else {
                    map.put(fieldName, value.toString());
                }
            } else {
                objectTransfer(timeFormatStr, value, map, excludeFields);
            }
        }
        return map;
    }
}
