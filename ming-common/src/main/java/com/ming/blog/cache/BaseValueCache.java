package com.ming.blog.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 提供针对Key为String的Value类型的通用操作。
 * <p>
 * 1. 子类需要复写getPrefix方法来提供自身的缓存key前缀。
 * 2. 子类需要复写getRedisTemplate方法来提供对应的RedisTemplate。
 * 3. 子类可以选择复写getTTL来提供超时时间的支持。不提供默认没有超时时间。
 * <p>
 * public V get(String key); 获取单个Value
 * public List<V> mutGet(List<String> keys);批量获取Value
 * public void set(String key, V value);设置单个Value
 * public void mutSet(Map<String, V> values); 批量设置Value缓存
 * public void drop(String key);删除单个Value
 *
 * @author unknown
 */

public abstract class BaseValueCache<K, V> extends BaseCache<K, V> {

    public V get(K key) {
        RedisTemplate<String, V> redisTemplate = getRedisTemplate();
        String cacheKey = buildKey(key);
        return CacheUtil.val().get(redisTemplate, cacheKey);
    }

    public V get() {
        return get(null);
    }

    public void set(V value) {
        set(null, value);
    }

    @Override
    public void drop() {
        drop(null);
    }

    public List<V> mutGet(List<K> keys) {
        if (keys == null || keys.isEmpty()) {
            return Lists.newArrayList();
        }
        RedisTemplate<String, V> redisTemplate = getRedisTemplate();
        List<String> cacheKeys = Lists.newArrayList();
        cacheKeys.addAll(keys.stream().map(this::buildKey).collect(Collectors.toList()));
        return CacheUtil.val().mutGet(redisTemplate, cacheKeys);
    }

    public void mutSet(Map<K, V> values) {
        Long ttl = getTTL();
        if (ttl == null) {
            RedisTemplate<String, V> redisTemplate = getRedisTemplate();
            Map<String, V> items = Maps.newHashMap();
            for (K key : values.keySet()) {
                items.put(buildKey(key), values.get(key));
            }
            CacheUtil.val().mutSet(redisTemplate, items);
        } else {
            // 批量set 如果有ttl 用 Pipelined 批量设置
            getRedisTemplate().executePipelined((RedisCallback<Object>) connection -> {
                StringRedisSerializer keySerializer = (StringRedisSerializer) getRedisTemplate().getKeySerializer();
                RedisSerializer valueSerializer = getRedisTemplate().getValueSerializer();
                for (Map.Entry<K, V> entry : values.entrySet()) {
                    if (valueSerializer instanceof FastJsonRedisSerializer) {
                        FastJsonRedisSerializer fastJsonRedisSerializer = (FastJsonRedisSerializer) valueSerializer;
                        connection.set(Objects.requireNonNull(keySerializer.serialize(buildKey(entry.getKey()))), Objects.requireNonNull(fastJsonRedisSerializer.serialize(entry.getValue())),
                                Expiration.seconds(ttl), RedisStringCommands.SetOption.UPSERT);
                    } else if (valueSerializer instanceof StringRedisSerializer) {
                        StringRedisSerializer stringRedisSerializer = (StringRedisSerializer) valueSerializer;
                        connection.set(Objects.requireNonNull(keySerializer.serialize(buildKey(entry.getKey()))),
                                Objects.requireNonNull(stringRedisSerializer.serialize(String.valueOf(entry.getValue()))), Expiration.seconds(ttl), RedisStringCommands.SetOption.UPSERT);
                    }
                }
                return null;
            });
        }
    }

    public Boolean setNx(K key, V value) {
        if (value == null) {
            return false;
        }
        RedisTemplate<String, V> redisTemplate = getRedisTemplate();
        String cacheKey = buildKey(key);
        return CacheUtil.val().setNx(redisTemplate, cacheKey, value);
    }

    public void set(K key, V value, Long ttl) {
        if (value != null) {
            RedisTemplate<String, V> redisTemplate = getRedisTemplate();
            String cacheKey = buildKey(key);
            if (ttl == null) {
                ttl = getTTL();
            }
            if (ttl == null) {
                CacheUtil.val().set(redisTemplate, cacheKey, value);
            } else {
                CacheUtil.val().set(redisTemplate, cacheKey, value, ttl);
            }
        }
    }

    public void set(K key, V value) {
        RedisTemplate<String, V> redisTemplate = getRedisTemplate();
        String cacheKey = buildKey(key);
        Long ttl = getTTL();
        if (ttl == null) {
            CacheUtil.val().set(redisTemplate, cacheKey, value);
        } else {
            CacheUtil.val().set(redisTemplate, cacheKey, value, ttl);
        }
    }
}

