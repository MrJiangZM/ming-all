package com.ming.blog.cache;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author unknown
 */
public class ValueUtil {
    private static Logger logger = LoggerFactory.getLogger(ValueUtil.class);

    public <K, T> void set(RedisTemplate<K, T> template, K key, T value, Long ttl) {
        try {
            template.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
    }

    public <K, T> void set(RedisTemplate<K, T> template, K key, T value) {
        try {
            template.opsForValue().set(key, value);
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
    }

    public <K, T> Boolean setNx(RedisTemplate<K, T> template, K key, T value) {
        try {
            return template.opsForValue().setIfAbsent(key, value);
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
        return false;
    }


    public <K, T> T get(RedisTemplate<K, T> template, K key) {
        try {
            return template.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("cache exception", e);
            return null;
        }
    }

    public <K, T> T get(RedisTemplate<K, T> template, K key, Long ttl) {
        try {
            template.expire(key, ttl, TimeUnit.SECONDS);
            return template.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("cache exception", e);
            return null;
        }
    }

    public <K, T> List<T> mutGet(RedisTemplate<K, T> template, List<K> keys) {
        if (keys == null || keys.isEmpty()) {
            return Lists.newArrayList();
        }
        try {
            return template.opsForValue().multiGet(keys);
        } catch (Exception e) {
            logger.error("cache exception when mutGet", e);
            return Lists.newArrayList();
        }
    }

    public <K, T> void mutSet(RedisTemplate<K, T> template, Map<K, T> values) {
        if (!CollectionUtils.isEmpty(values)) {
            try {
                template.opsForValue().multiSet(values);
            } catch (Exception e) {
                logger.error("cache exception when mutSet", e);
            }
        }
    }

    public <K, T> void increment(RedisTemplate<K, T> template, K key, int value) {
        try {
            template.opsForValue().increment(key, value);
        } catch (Exception e) {
            logger.error("cache exception when increment", e);
        }
    }

    public <K, T> void decrement(RedisTemplate<K, T> template, K key, int value) {
        try {
            template.opsForValue().decrement(key, value);
        } catch (Exception e) {
            logger.error("cache exception when decrement", e);
        }
    }
}
