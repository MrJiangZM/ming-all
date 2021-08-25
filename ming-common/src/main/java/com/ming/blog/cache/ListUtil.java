package com.ming.blog.cache;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * listAll util
 * 1. add和mutAdd方法保证两点。a: 列表中有且只有一个PlaceHolder, b:放入实际元素时移除PlaceHolder。
 * 2. 由于1, 所以在获取数据时不用对PlaceHolder进行移除,但是要考虑列表中只有PlaceHolder的情况,
 * 例如size方法,如果列表中只有PlaceHolder, 那么就返回0, list方法,如果列表中只有PlaceHolder,那么就返回空列表。
 * 为了减少缓存访问次数,list方法中判断只有PlaceHolder时并不是采用isOnlyPlaceHolder方法, 而是根据读取的元素去判断。
 */
public class ListUtil {

    private static Logger logger = LoggerFactory.getLogger(ListUtil.class);

    public static <K, T> List<T> listAll(RedisTemplate<K, T> template, K key) {
        return list(template, key, 0, 0);
    }

    public static <K, T> List<T> list(RedisTemplate<K, T> template, K key, int start, int size) {
        try {
            if (!template.hasKey(key)) {
                return null;
            }
            int end = (start + size - 1);
            List<T> values = template.opsForList().range(key, start, end);
            if (values != null && values.size() == 1) {
                T cached = values.get(0);
                if (CacheUtil.isPlaceHolder(template, cached)) {
                    return Lists.newArrayList();
                }
            }
            return values;
        } catch (Exception e) {
            logger.error("cache exception", e);
            return null;
        }
    }

    public static <K, T> void add(RedisTemplate<K, T> template, K key, T value) {
        add(template, key, value, null);
    }

    public static <K, T> void add(RedisTemplate<K, T> template, K key, T value, Long ttl) {
        List<T> values = Lists.newArrayList();
        if (value != null) {
            values.add(value);
        }
        mutAdd(template, key, values, ttl);
    }

    public static <K, T> void mutAdd(RedisTemplate<K, T> template, K key, List<T> values) {
        mutAdd(template, key, values, null);
    }

    public static <K, T> void mutAdd(RedisTemplate<K, T> template, K key, List<T> values, Long ttl) {

        if (values == null || values.isEmpty()) {
            values = Lists.newArrayList();
            values.add(CacheUtil.getPlaceHolderValue(template));
        }
        if (isOnlyPlaceHolder(template, key)) { //如果只占位符就删除掉
            rightPop(template, key);
        }

        try {
            template.opsForList().rightPushAll(key, values);
            if (ttl != null) {
                template.expire(key, ttl, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
    }

    public static <K, T> Long size(RedisTemplate<K, T> template, K key) {
        try {
            if (!template.hasKey(key)) {
                return null;
            }
            if (isOnlyPlaceHolder(template, key)) {
                return 0L;
            }
            return template.opsForList().size(key);
        } catch (Exception e) {
            logger.error("cache exception", e);
            return null;
        }
    }

    private static <K, T> boolean isOnlyPlaceHolder(RedisTemplate<K, T> template, K key) {
        try {
            Long cacheSize = template.opsForList().size(key);
            if (cacheSize != null && cacheSize == 1) {
                T cached = Objects.requireNonNull(template.opsForList().range(key, 0, -1)).get(0);
                return CacheUtil.isPlaceHolder(template, cached);
            }
            return false;
        } catch (Exception e) {
            logger.error("cache exception", e);
            return false;
        }
    }

    public static <K, T> void leftPush(RedisTemplate<K, T> template, K key, T value) {

        if (value == null) {
            value = CacheUtil.getPlaceHolderValue(template);
        }
        template.opsForList().leftPush(key, Objects.requireNonNull(value));
    }

    public static <K, T> T rightPop(RedisTemplate<K, T> template, K key) {
        try {
            return template.opsForList().rightPop(key);
        } catch (Exception e) {
            logger.error("cache exception", e);
            return null;
        }
    }
}

