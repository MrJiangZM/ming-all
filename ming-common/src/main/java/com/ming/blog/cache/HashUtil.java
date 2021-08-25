package com.ming.blog.cache;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HashUtil {

    private static Logger logger = LoggerFactory.getLogger(HashUtil.class);

    public <K, HK, HV> HV get(RedisTemplate<K, HV> template, K key, HK hashKey) {
        try {
            HashOperations<K, HK, HV> hash = template.opsForHash();
            return hash.get(key, hashKey);
        } catch (Exception e) {
            logger.error("cache exception", e);
            return null;
        }
    }

    public <K, HK, HV> void put(RedisTemplate<K, HV> template, K key, HK hashKey, HV value) {
        if (value != null) {
            try {
                template.opsForHash().put(key, hashKey, value);
            } catch (Exception e) {
                logger.error("cache exception", e);
            }
        }
    }

    public <K, HK, HV> List<HV> mutGet(RedisTemplate<K, HV> template, K key, List<HK> hashKeys) {
        if (hashKeys == null || hashKeys.isEmpty()) {
            return Lists.newArrayList();
        }
        try {
            HashOperations<K, HK, HV> hash = template.opsForHash();
            return hash.multiGet(key, hashKeys);
        } catch (Exception e) {
            logger.error("cache exception", e);
            return null;
        }
    }

    public <K, HK, HV> Map<HK, HV> entries(RedisTemplate<K, HV> template, K key) {
        try {
            if (!template.hasKey(key)) {
                return null;
            }
            HashOperations<K, HK, HV> hash = template.opsForHash();
            return hash.entries(key);
        } catch (Exception e) {
            logger.error("cache exception", e);
            return null;
        }
    }

    public <K, HK, HV> List<HV> values(RedisTemplate<K, HV> template, K key) {
        try {
            HashOperations<K, HK, HV> hash = template.opsForHash();
            return hash.values(key);
        } catch (Exception e) {
            logger.error("cache exception", e);
            return Lists.newArrayList();
        }
    }

    public <K, HK, HV> void increment(RedisTemplate<K, HV> template, K key, HK hashKey, Long score) {
        try {
            HashOperations<K, HK, HV> hash = template.opsForHash();
            hash.increment(key, hashKey, score);
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
    }

    public <K, HK, HV> void delete(RedisTemplate<K, HV> template, K key, HK hashKey) {
        try {
            HashOperations<K, HK, HV> hash = template.opsForHash();
            hash.delete(key, hashKey);
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
    }

    public <K, HK, HV> void putAll(RedisTemplate<K, HV> template, K key, Map<HK, HV> map) {
        putAll(template, key, map, null);
    }

    public <K, HK, HV> void putAll(RedisTemplate<K, HV> template, K key, Map<HK, HV> map, Long ttl) {
        try {
            if (map == null || map.isEmpty()) {
                return;
            }
            if (template.hasKey(key)) {
                template.delete(key);
            }
            HashOperations<K, HK, HV> hash = template.opsForHash();
            hash.putAll(key, map);
            if (ttl != null) {
                template.expire(key, ttl, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
    }

}

