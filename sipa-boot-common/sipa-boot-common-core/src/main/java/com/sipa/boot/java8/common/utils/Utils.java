package com.sipa.boot.java8.common.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author zhouxiajie
 * @date 2019-02-20
 */
public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static void checkArgument(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static String stringValueOf(Object obj) {
        return obj == null ? null : trim(obj);
    }

    private static String trim(Object obj) {
        String as = String.valueOf(obj);
        return "null".equalsIgnoreCase(as) ? null : as;
    }

    public static String increase(String intValueString) {
        LOGGER.info("origin string value is [{}]", intValueString);
        try {
            int value = Integer.parseInt(intValueString);
            value++;
            LOGGER.info("increased string value is [{}]", value);
            return String.valueOf(value);
        } catch (NumberFormatException e) {
            LOGGER.warn("increase failed, input value is not an int value");
        }
        return intValueString;
    }

    /**
     * 转换13位时间戳
     *
     * @param timestamp
     *            时间戳
     * @return 13位时间戳
     */
    public static long convertTimestampTo13(long timestamp) {
        if (String.valueOf(timestamp).length() == SipaBootCommonConstants.Number.INT_10) {
            return 1000L * timestamp;
        } else {
            return timestamp;
        }
    }

    /**
     * 转换10位时间戳
     *
     * @param timestamp
     *            时间戳
     * @return 10位时间戳
     */
    public static long convertTimestampTo10(long timestamp) {
        if (String.valueOf(timestamp).length() == SipaBootCommonConstants.Number.INT_13) {
            return timestamp / 1000L;
        } else {
            return timestamp;
        }
    }

    /**
     * 校验位数
     *
     * @param args
     *            参数
     * @param len
     *            长度
     * @return 校验结果
     */
    public static boolean checkLength(@NonNull String args, int len) {
        return args.length() == len;
    }

    /**
     * object暗转转换为list<T>
     *
     * @param src
     *            转换对象
     * @param clazz
     *            目标class
     * @return 转换后list 转换失败会返回null
     */
    public static <T> List<T> castList(Object src, Class<T> clazz) {
        if (src instanceof List<?>) {
            return ((List<?>)src).stream().map(clazz::cast).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * obj to map.
     *
     * @param obj
     *            object
     * @return map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();

        if (Objects.nonNull(obj)) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(obj.getClass());
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {
                    String key = property.getName();

                    if (key.compareToIgnoreCase("class") == 0) {
                        continue;
                    }

                    Method getter = property.getReadMethod();
                    Object value = getter != null ? getter.invoke(obj) : null;
                    map.put(key, value);
                }
            } catch (IntrospectionException e) {
                LOGGER.error("Utils.objectToMap -> Introspector.getBeanInfo fail", e);
                map = new HashMap<>();
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("Utils.objectToMap -> getter.invoke(obj) fail", e);
                map = new HashMap<>();
            }
        }

        return map;
    }
}
