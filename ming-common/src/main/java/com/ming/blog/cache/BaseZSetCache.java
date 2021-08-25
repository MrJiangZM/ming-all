package com.ming.blog.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class BaseZSetCache<K, V> extends BaseCache<K, V> implements RemovableListCache<K, V> {
    public Map<K, List<V>> batchGet(List<K> keys) {
        List<Object> objects = getRedisTemplate().executePipelined((RedisCallback<Object>) connection -> {
            StringRedisSerializer keySerializer = (StringRedisSerializer) getRedisTemplate().getKeySerializer();
            for (K key : keys) {
                connection.zRevRange(Objects.requireNonNull(keySerializer.serialize(buildKey(key))), 0, Integer.MAX_VALUE);
            }
            return null;
        });
        Map<K, List<V>> result = new HashMap<>();
        for (int i = 0; i < objects.size(); i++) {
            Set<V> set = (LinkedHashSet<V>) objects.get(i);
            if (set.isEmpty()) {
                if (exist(keys.get(i))) {
                    result.put(keys.get(i), Lists.newArrayList());
                }
            } else {
                result.put(keys.get(i), new ArrayList<>(set));
            }
        }
        return result;
    }

    public List<V> reverseList(int start, int length) {
        return reverseList(null, start, length);
    }

    public List<V> reverseList(K key, int start, int length) {
        String cacheKey = buildKey(key);
        return CacheUtil.zset().reverseList(getRedisTemplate(), cacheKey, start, length);
    }

    public List<ValueScorePair<V>> reverseAllListWithScore() {
        return reverseAllListWithScore(null);
    }

    public List<ValueScorePair<V>> reverseAllListWithScore(K key) {
        String cacheKey = buildKey(key);
        return CacheUtil.zset().reverseListAllWithScore(getRedisTemplate(), cacheKey);
    }

    public List<ValueScorePair<V>> reverseListWithScore(int start, int size) {
        return reverseListWithScore(null, start, size);
    }

    public List<ValueScorePair<V>> reverseListWithScore(K key, int start, int size) {
        String cacheKey = buildKey(key);
        return CacheUtil.zset().reverseListWithScore(getRedisTemplate(), cacheKey, start, size);
    }

    public List<V> listByScore(K key, double minScore, double maxScore) {
        String cacheKey = buildKey(key);
        return CacheUtil.zset().listByScoreFromZSet(getRedisTemplate(), cacheKey, minScore, maxScore);
    }

    public List<V> listByScore(double minScore, double maxScore) {
        String cacheKey = buildKey(null);
        return CacheUtil.zset().listByScoreFromZSet(getRedisTemplate(), cacheKey, minScore, maxScore);
    }

    public List<V> list(int start, int length) {
        return list(null, start, length);
    }

    public List<V> list(K key, int start, int length) {
        String cacheKey = buildKey(key);
        return CacheUtil.zset().list(getRedisTemplate(), cacheKey, start, length);
    }

    public List<V> listAll() {
        return listAll(null);
    }

    public List<V> listAll(K key) {
        String cacheKey = buildKey(key);
        return CacheUtil.zset().list(getRedisTemplate(), cacheKey, 0, 0);
    }


    public void addIfListExist(V value, Double score) {
        addIfListExist(null, value, score);
    }

    public void addIfListExist(K key, V value, Double score) {
        if (exist(key)) {
            add(key, value, score);
        }
    }

    public void addIfElementExist(V value, Double score) {
        addIfElementExist(null, value, score);
    }

    public void addIfElementExist(K key, V value, Double score) {
        String cacheKey = buildKey(key);
        if (CacheUtil.zset().isMember(getRedisTemplate(), cacheKey, value)) {
            add(key, value, score);
        }
    }

    public void add(V value, Double score) {
        add(null, value, score);
    }

    public void add(K key, V value, Double score) {
        if (value != null) {
            mutAdd(key, Lists.newArrayList(new ValueScorePair<>(value, score)));
        }
    }

    public void mutAddIfExist(List<ValueScorePair<V>> valueScorePairs) {
        mutAddIfExist(null, valueScorePairs);
    }

    public void mutAddIfExist(K key, List<ValueScorePair<V>> valueScorePairs) {
        if (exist(key)) {
            mutAdd(key, valueScorePairs);
        }
    }

    public void mutAdd(List<ValueScorePair<V>> valueScorePairs) {
        mutAdd(null, valueScorePairs);
    }

    public void mutAdd(K key, List<ValueScorePair<V>> valueScorePairs) {
        String cacheKey = buildKey(key);
        Set<ZSetOperations.TypedTuple<V>> valueSets = Sets.newHashSet();
        for (ValueScorePair<V> valueScorePair : valueScorePairs) {
            ZSetOperations.TypedTuple<V> typedTuple = new DefaultTypedTuple<>(valueScorePair.getValue(), valueScorePair.getScore());
            valueSets.add(typedTuple);
        }
        Long ttl = getTTL();
        if (ttl != null) {
            CacheUtil.zset().mutAdd2ZSet(getRedisTemplate(), cacheKey, valueSets, ttl);
        } else {
            CacheUtil.zset().mutAdd2ZSet(getRedisTemplate(), cacheKey, valueSets);
        }
    }

    @Override
    public void mutRemove(List<V> values) {
        mutRemove(null, values);
    }

    @Override
    public void mutRemove(K key, List<V> values) {
        String cacheKey = buildKey(key);
        CacheUtil.zset().mutDeleteFromZSet(getRedisTemplate(), cacheKey, values);
    }

    @Override
    public void remove(V value) {
        remove(null, value);
    }

    @Override
    public void remove(K key, V value) {
        String cacheKey = buildKey(key);
        CacheUtil.zset().deleteFromZSet(getRedisTemplate(), cacheKey, value);
    }

    public Long size(K key) {
        String cacheKey = buildKey(key);
        return CacheUtil.zset().getCountOfZSet(getRedisTemplate(), cacheKey);
    }

    public Long size() {
        return size(null);
    }

    public Double getScore(K key, V value) {
        String cacheKey = buildKey(key);
        return CacheUtil.zset().getScore(getRedisTemplate(), cacheKey, value);
    }

    public Double getScore(V value) {
        String cacheKey = buildKey(null);
        return CacheUtil.zset().getScore(getRedisTemplate(), cacheKey, value);
    }

    public boolean isMember(K key, V value) {
        String cacheKey = buildKey(key);
        return CacheUtil.zset().isMember(getRedisTemplate(), cacheKey, value);
    }
}
