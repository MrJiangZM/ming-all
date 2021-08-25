package com.ming.blog.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SetUtil {

    private static Logger logger = LoggerFactory.getLogger(SetUtil.class);

    public <K, T> void add(RedisTemplate<K, T> template, K key, T candidate) {
        try {
            template.opsForSet().add(key, candidate);
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
    }

    public <K, T> void addAll(RedisTemplate<K, T> template, K key, T[] candidate) {
        addAll(template, key, candidate, null);
    }

    public <K, T> void addAll(RedisTemplate<K, T> template, K key, T[] candidate, Long ttl) {
        try {
            if (candidate == null || candidate.length == 0) {
                template.opsForSet().add(key, CacheUtil.getPlaceHolderValue(template));
            } else {
                template.opsForSet().add(key, candidate);
            }
            if (ttl != null) {
                template.expire(key, ttl, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
    }

    public <K, T> boolean isMember(RedisTemplate<K, T> template, K key, T value) {
        try {
            return template.opsForSet().isMember(key, value);
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
        return false;
    }

    public <K, T> void remove(RedisTemplate<K, T> template, K key, T value) {
        try {
            template.opsForSet().remove(key, value);
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
    }

    public <K, T> Set<T> members(RedisTemplate<K, T> template, K key) {
        try {
            return template.opsForSet().members(key);
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
        return null;
    }
}

