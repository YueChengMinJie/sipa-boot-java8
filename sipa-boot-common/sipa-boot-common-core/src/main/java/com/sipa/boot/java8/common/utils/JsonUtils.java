package com.sipa.boot.java8.common.utils;

import java.io.IOException;
import java.util.Objects;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author zhouxiajie
 * @date 2019-03-19
 */
public class JsonUtils {
    public static String writeValueAsString(ObjectMapper objectMapper, Object object) {
        try {
            if (Objects.nonNull(object)) {
                return objectMapper.writeValueAsString(object);
            }
        } catch (JsonProcessingException ignored) {
        }
        return null;
    }

    public static <T> T convertValue(ObjectMapper objectMapper, String jsonStr, Class<T> clazz) {
        try {
            if (Objects.nonNull(jsonStr)) {
                JSONObject myJson = JSONObject.parseObject(jsonStr);
                return objectMapper.convertValue(myJson, clazz);
            }
        } catch (JSONException ignored) {
        }
        return null;
    }

    /**
     * Spring 环境下可用
     */
    public static String writeValueAsString(Object object) {
        return writeValueAsString(AppUtils.getBean(ObjectMapper.class), object);
    }

    /**
     * Spring 环境下可用
     */
    public static <T> T convertValue(String jsonStr, Class<T> clazz) {
        return convertValue(AppUtils.getBean(ObjectMapper.class), jsonStr, clazz);
    }

    /**
     * Spring 环境下可用
     */
    public static <T> T convertValue(String jsonStr, TypeReference<T> javaType) {
        try {
            return AppUtils.getBean(ObjectMapper.class).readValue(jsonStr, javaType);
        } catch (IOException e) {
            return null;
        }
    }
}
