package com.sipa.boot.java8.data.redis.utils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sipa.boot.java8.common.utils.Utils;

/**
 * redis 工具类
 *
 * @author sunyukun
 * @date 2019/2/24
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class RedisUtils {
    private static RedisTemplate<String, Object> redisTemplate;

    private static StringRedisSerializer redisSerializer;

    @Autowired
    public RedisUtils(RedisTemplate<String, Object> redisTemplate, StringRedisSerializer redisSerializer) {
        RedisUtils.redisTemplate = redisTemplate;
        RedisUtils.redisSerializer = redisSerializer;
    }

    /**
     * 批量获取redis.
     *
     * @param key
     *            main key of redis
     * @return detail key-value map.
     */
    public static Map<Object, Object> entries(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 批量获取redis.
     *
     * @param key
     *            main key of redis
     * @param hashKeySet
     *            detail key of redis
     * @return detail key-value map.
     */
    public static Map<String, Object> hashBatchGet(String key, Set<String> hashKeySet) {
        Map<String, Object> result = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(hashKeySet)) {
            hashKeySet.forEach(hashKey -> result.put(hashKey, hGet(key, hashKey)));
        }
        return result;
    }

    /**
     * 批量更新redis.
     *
     * @param key
     *            main key of redis
     * @param hashMap
     *            detail key-value map.
     */
    public static void hashBatchUpdate(String key, Map<String, Object> hashMap) {
        if (MapUtils.isNotEmpty(hashMap)) {
            Map<String, Object> addUpdateMap = new HashMap<>(16);

            for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
                String k = entry.getKey();
                Object v = entry.getValue();
                if (Objects.nonNull(v)) {
                    addUpdateMap.put(k, v);
                }
            }

            if (MapUtils.isNotEmpty(addUpdateMap)) {
                redisTemplate.opsForHash().putAll(key, addUpdateMap);
            }
        }
    }

    /**
     * key是否存在.
     *
     * @param key
     *            key of redis
     * @return is exists.
     */
    public static Boolean isExists(String key) {
        return redisTemplate.hasKey(Objects.requireNonNull(key));
    }

    /**
     * 获取value.
     *
     * @param key
     *            key of redis
     * @return string value.
     */
    public static String get(String key) {
        return Utils.stringValueOf(redisTemplate.opsForValue().get(key));
    }

    /**
     * 获取value.
     *
     * @param key
     *            key of redis
     * @return list
     */
    public static List<Object> getList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 根据vin 扫表
     *
     * @param key
     *            vin
     * @return String
     */
    public static Set<String> scan(String key) {
        Set<String> ks = new HashSet<>();
        Set<String> keys = redisTemplate.keys(key + "*");
        Set<String> keys2 = redisTemplate.keys("*" + key);
        if (CollectionUtils.isNotEmpty(keys)) {
            ks.addAll(keys);
        }
        if (CollectionUtils.isNotEmpty(keys2)) {
            ks.addAll(keys2);
        }
        return ks;
    }

    /**
     * 获取hash的keys
     *
     * @param key
     *            key
     * @return hkeys
     */
    public static Set<Object> hashKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取hash value.
     *
     * @param key
     *            key of redis
     * @param hashKey
     *            hash key of redis
     * @return value of hash key.
     */
    public static String hGet(String key, String hashKey) {
        return Utils.stringValueOf(redisTemplate.opsForHash().get(key, hashKey));
    }

    /**
     * 设值hash value.
     *
     * @param key
     *            key of redis
     * @param hashKey
     *            hash key of redis
     * @param hashValue
     *            hash value of redis.
     */
    public static void hSet(String key, String hashKey, Object hashValue) {
        hSet(key, hashKey, hashValue, -1L);
    }

    /**
     * 设值hash value.
     *
     * @param key
     *            key of redis
     * @param hashKey
     *            hash key of redis
     * @param hashValue
     *            hash value of redis
     * @param timeoutSec
     *            hash key time out.
     */
    public static void hSet(String key, String hashKey, Object hashValue, Long timeoutSec) {
        redisTemplate.opsForHash().put(key, hashKey, hashValue);
        if (timeoutSec > 0L) {
            redisTemplate.expire(key, timeoutSec, TimeUnit.SECONDS);
        }
    }

    /**
     * hash set All
     *
     * @param key
     *            key
     * @param map
     *            map
     */
    public static void hSetAll(String key, Map<Object, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 赋值key于value.
     *
     * @param key
     *            key of redis
     * @param value
     *            value of key.
     */
    public static void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * batch set
     *
     * @param keyAndValue
     *            key value map
     */
    public static void batchSet(Map<String, String> keyAndValue) {
        redisTemplate.opsForValue().multiSet(keyAndValue);
    }

    /**
     * 赋值key于value.
     *
     * @param key
     *            key of redis
     * @param value
     *            value of key.
     */
    public static void set(String key, Object value, Long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 删除key.
     *
     * @param key
     *            key of redis.
     */
    public static void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除.
     *
     * @param keys
     *            key of redis.
     */
    public static void deleteKeys(Set<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 删除hset里面的key
     *
     * @param key
     *            key
     * @param objects
     *            hashKey.
     */
    public static void deleteHashKey(String key, Object[] objects) {
        redisTemplate.opsForHash().delete(key, objects);
    }

    /**
     * 升1.
     *
     * @param key
     *            key of redis.
     */
    public static Long increase(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 降1.
     *
     * @param key
     *            key of redis.
     */
    public static void decrement(String key) {
        redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 获取hash.
     *
     * @param key
     *            key of redis
     * @return hash entries.
     */
    public static Map<String, String> hGetAll(String key) {
        Map<String, String> hash = new HashMap<>(128);

        Cursor<Map.Entry<Object, Object>> cursor = null;

        try {
            cursor = redisTemplate.opsForHash().scan(key, ScanOptions.NONE);

            while (cursor.hasNext()) {
                Map.Entry<Object, Object> entry = cursor.next();

                if (entry.getKey() != null) {
                    hash.put(Utils.stringValueOf(entry.getKey()), Utils.stringValueOf(entry.getValue()));
                }
            }
        } finally {
            closeQuietly(cursor);
        }

        return hash;
    }

    private static void closeQuietly(Cursor<Map.Entry<Object, Object>> cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * set加个value.
     *
     * @param k
     *            key of redis
     * @param v
     *            value of key.
     */
    public static void sAdd(String k, String v) {
        redisTemplate.opsForSet().add(k, v);
    }

    /**
     * 升固定值
     *
     * @param k
     *            key of redis
     * @param v
     *            value of key.
     */
    public static void increase(String k, long v) {
        redisTemplate.opsForValue().increment(k, v);
    }

    /**
     * 降固定值
     *
     * @param k
     *            key of redis
     * @param v
     *            value of key.
     */
    public static void decrement(String k, long v) {
        redisTemplate.opsForValue().decrement(k, v);
    }

    /**
     * The multi get, usually used to get strings.
     *
     * @param keys
     *            key list
     * @return list of template value type.
     */
    public static List<Object> multiGet(List<String> keys) {
        if (CollectionUtils.isNotEmpty(keys)) {
            return redisTemplate.opsForValue().multiGet(keys);
        }
        return null;
    }

    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     *            key
     * @param value
     *            值
     * @param score
     *            权值
     * @return 是否成功
     */
    public Boolean zAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * @param key
     *            key
     * @param values
     *            值数组
     * @return 数量
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 获取集合大小
     *
     * @param key
     *            key
     * @return 长度
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取集合的元素, 从小到大排序
     *
     * @param key
     *            key
     * @param start
     *            开始位置
     * @param end
     *            结束位置, -1查询所有
     * @return 值集合
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 移除指定索引位置的成员
     *
     * @param key
     *            key
     * @param start
     *            开始位置
     * @param end
     *            结束位置
     * @return 剩余数量
     */
    public Long zRemoveRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 根据指定的score值的范围来移除成员
     *
     * @param key
     *            key
     * @param min
     *            最小值
     * @param max
     *            最大值
     * @return 剩余数量
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    public static void addListValue(String key, Object o) {
        redisTemplate.opsForList().rightPush(key, o);
    }

    public static void addListFirstValue(String key, Object o) {
        redisTemplate.opsForList().leftPush(key, o);
    }

    public static Object removeListLastItem(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    public static byte[] serialize(String kv) {
        return Objects.requireNonNull(redisSerializer.serialize(kv));
    }

    public static RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    // *******************************************************************
    // ************************* lock and unlock *************************
    // *******************************************************************

    public static boolean tryGetDistributedLock(String lockKey, String requestId, int expireTimeSeconds) {
        return Optional
            .ofNullable(
                redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTimeSeconds, TimeUnit.SECONDS))
            .orElse(Boolean.FALSE);
    }

    public static boolean releaseDistributedLock(String lockKey, String requestId) {
        String script =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);

        return Optional.ofNullable(redisTemplate.execute(redisScript, Lists.newArrayList(lockKey), requestId))
            .orElse(Boolean.FALSE);
    }
}
