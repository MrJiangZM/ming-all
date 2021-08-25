package com.ming.blog.cache;

import com.google.common.collect.Maps;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author unknown
 */
public abstract class BaseHashCache<K, HK, V> extends BaseCache<K, V> {
    /**
     * 批量获取hash缓存中多个hk的值
     *
     * @param keys
     * @param hks
     *
     * @return
     */
    public Map<K, Map<HK, V>> batchGet(List<K> keys, List<HK> hks) {

        List<Object> objects = getRedisTemplate().executePipelined((RedisCallback<Object>) connection -> {
            StringRedisSerializer keySerializer = (StringRedisSerializer) getRedisTemplate().getKeySerializer();
            RedisSerializer<HK> hkSerializer = (RedisSerializer<HK>) (getRedisTemplate().getHashKeySerializer());
            byte[][] hkBytes = new byte[hks.size()][];
            for (int i = 0; i < hks.size(); i++) {
                HK hk = hks.get(i);
                hkBytes[i] = hkSerializer.serialize(hk);
            }
            for (K key : keys) {
                connection.hMGet(Objects.requireNonNull(keySerializer.serialize(buildKey(key))), hkBytes);
            }
            return null;
        });
        Map<K, Map<HK, V>> result = new HashMap<>();
        for (int i = 0; i < objects.size(); i++) {
            Map<HK, V> map = Maps.newHashMap();
            List<V> set = (ArrayList<V>) objects.get(i);
            for (int j = 0; j < set.size(); j++) {
                if (set.get(j) != null) {
                    map.put(hks.get(j), set.get(j));
                }
            }
            /*取出来的内容为空,但是hash缓存本身存在,则放一个空map返回, 否则不放入map,
             * 调用方可以根据key对应的值为null判定该hash缓存不存在。例如:
             */
            if (map.isEmpty()) {
                if (exist(keys.get(i))) {
                    result.put(keys.get(i), map);
                }
            } else {
                result.put(keys.get(i), map);
            }
        }
        return result;
    }

    public V get(K key, HK hashKey) {
        String cacheKey = buildKey(key);
        return CacheUtil.hash().get(getRedisTemplate(), cacheKey, hashKey);
    }

    public Map<HK, V> mutGet(K key, List<HK> hashKey) {
        String cacheKey = buildKey(key);
        List<V> list = CacheUtil.hash().mutGet(getRedisTemplate(), cacheKey, hashKey);
        Map<HK, V> result = Maps.newHashMap();
        for (int i = 0; i < hashKey.size(); i++) {
            result.put(hashKey.get(i), list.get(i));
        }
        return result;
    }

    public void put(K key, HK hashKey, V value) {
        RedisTemplate<String, V> template = getRedisTemplate();
        String cacheKey = buildKey(key);
        CacheUtil.hash().put(template, cacheKey, hashKey, value);
    }

    public Map<HK, V> entries(K key) {
        RedisTemplate<String, V> template = getRedisTemplate();
        String cacheKey = buildKey(key);
        return CacheUtil.hash().entries(template, cacheKey);
    }

    public List<V> values(K key) {
        RedisTemplate<String, V> template = getRedisTemplate();
        String cacheKey = buildKey(key);
        return CacheUtil.hash().values(template, cacheKey);
    }

    public void putAll(K key, Map<HK, V> map) {
        RedisTemplate<String, V> template = getRedisTemplate();
        String cacheKey = buildKey(key);
        CacheUtil.hash().putAll(template, cacheKey, map);
        if (getTTL() != null) {
            template.expire(cacheKey, getTTL(), TimeUnit.SECONDS);
        }
    }

    public void delete(K key, HK hashKey) {
        RedisTemplate<String, V> template = getRedisTemplate();
        String cacheKey = buildKey(key);
        CacheUtil.hash().delete(template, cacheKey, hashKey);
    }
}
