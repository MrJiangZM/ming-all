package com.ming.blog.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author unknown
 */
public class ZSetUtil {
    private static Logger logger = LoggerFactory.getLogger(ZSetUtil.class);

    /**
     * 批量向zset中加入元素。如果参数set为null或未空, 则将一个默认值放入zset中进行占位, 代表列表为空。
     *
     * @param template
     * @param key
     * @param set
     * @param <K>
     * @param <T>
     */
    public <K, T> void mutAdd2ZSet(RedisTemplate<K, T> template, K key, Set<ZSetOperations.TypedTuple<T>> set) {
        mutAdd2ZSet(template, key, set, null);
    }

    public <K, T> void mutAdd2ZSet(RedisTemplate<K, T> template, K key, Set<ZSetOperations.TypedTuple<T>> set, Long ttl) {
        try {
            T placeHolderValue = CacheUtil.getPlaceHolderValue(template);
            if (set == null || set.isEmpty()) {
                set = set == null ? Sets.newHashSet() : set;
                ZSetOperations.TypedTuple<T> typedTuple = new DefaultTypedTuple<>(placeHolderValue, -1d);
                set.add(typedTuple);
            } else {
                template.opsForZSet().remove(key, placeHolderValue);
            }
            template.opsForZSet().add(key, set);
            if (ttl != null) {
                template.expire(key, ttl, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error("cache exception when mutAdd2ZSet", e);
        }
    }

    public <K, T> Long remRangByRank(RedisTemplate<K, T> template, K key, int start, int end) {
        try {
            return template.opsForZSet().removeRange(key, start, end);
        } catch (Exception e) {
            logger.error("cache exception when remRangByRank", e);
        }
        return 0L;
    }

    /**
     * 根据偏移量从zset中获取数据。缓存中不存在参数key代表的缓存则返回null, 如果列表中仅有一个占位元素{@link ZSetUtil#mutAdd2ZSet}, 则返回空列表。
     * score倒排
     *
     * @param template
     * @param key
     * @param start
     * @param length
     * @param <K>
     * @param <T>
     *
     * @return listAll
     */
    public <K, T> List<T> reverseList(RedisTemplate<K, T> template, K key, int start, int length) {
        Set<T> set = reverseRange(template, key, start, length);
        if (set != null) {
            return new ArrayList<>(set);
        } else {
            return null;
        }
    }

    public <K, T> List<ValueScorePair<T>> reverseListAllWithScore(RedisTemplate<K, T> template, K key) {
        return reverseListWithScore(template, key, 0, 0);
    }

    public <K, T> List<ValueScorePair<T>> reverseListWithScore(RedisTemplate<K, T> template, K key, int start, int length) {
        List<ValueScorePair<T>> result;
        try {
            if (!template.hasKey(key)) {
                return null;
            }
            int end = (start + length - 1);
            Set<ZSetOperations.TypedTuple<T>> set = template.opsForZSet().reverseRangeWithScores(key, start, end);
            result = Lists.newArrayList();
            T placeHolderValue = CacheUtil.getPlaceHolderValue(template);
            for (ZSetOperations.TypedTuple<T> s : Objects.requireNonNull(set)) {
                if (!Objects.requireNonNull(s.getValue()).equals(placeHolderValue)) {
                    result.add(new ValueScorePair<>(s.getValue(), s.getScore()));
                }
            }
        } catch (Exception e) {
            logger.error("cache exception when listByScoreFromZSet", e);
            return Lists.newArrayList();
        }
        return result;
    }

    /**
     * @param template
     * @param key
     * @param start
     * @param length
     * @param <K>
     * @param <T>
     *
     * @return set
     */
    public <K, T> Set<T> reverseRange(RedisTemplate<K, T> template, K key, int start, int length) {
        try {
            if (!template.hasKey(key)) {
                return null;
            }
            int end = (start + length - 1);
            Set<T> idSet = template.opsForZSet().reverseRange(key, start, end);
            return removePlaceHolder(template, key, Objects.requireNonNull(idSet));
        } catch (Exception e) {
            logger.error("cache exception when reverseRange", e);
            return null;
        }
    }

    public <K, T> List<T> list(RedisTemplate<K, T> template, K key, int start, int length) {
        Set<T> set = range(template, key, start, length);
        if (set != null) {
            return new ArrayList<>(set);
        } else {
            return null;
        }
    }

    public <K, T> Set<T> range(RedisTemplate<K, T> template, K key, int start, int length) {
        try {
            if (!template.hasKey(key)) {
                return null;
            }
            int end = (start + length - 1);
            Set<T> idSet = template.opsForZSet().range(key, start, end);
            return removePlaceHolder(template, key, Objects.requireNonNull(idSet));
        } catch (Exception e) {
            logger.error("cache exception when range", e);
            return null;
        }
    }


    public <K, T> List<T> reverseListAll(RedisTemplate<K, T> template, K key) {
        return reverseList(template, key, 0, 0);
    }

    /**
     * @param template
     * @param key
     * @param minScore
     * @param maxScore
     * @param <K>
     * @param <T>
     *
     * @return
     */
    public <K, T> List<T> listByScoreFromZSet(RedisTemplate<K, T> template, K key, double minScore, double maxScore) {
        try {
            if (!template.hasKey(key)) {
                return null;
            }
            Set<T> records = template.opsForZSet().rangeByScore(key, minScore, maxScore);
            return new ArrayList<>(removePlaceHolder(template, key, Objects.requireNonNull(records)));
        } catch (Exception e) {
            logger.error("cache exception when listByScoreFromZSet", e);
            return Lists.newArrayList();
        }
    }

    public <K, T> List<T> listReverseByScoreFromZSet(RedisTemplate<K, T> template, K key, double minScore, double maxScore) {
        try {
            if (!template.hasKey(key)) {
                return null;
            }
            Set<T> records = template.opsForZSet().reverseRangeByScore(key, minScore, maxScore);
            return new ArrayList<>(removePlaceHolder(template, key, Objects.requireNonNull(records)));
        } catch (Exception e) {
            logger.error("cache exception when listByScoreFromZSet", e);
            return Lists.newArrayList();
        }
    }

    private <K, T> Set<T> removePlaceHolder(RedisTemplate<K, T> template, K key, Set<T> result) {
        T placeHolderValue = CacheUtil.getPlaceHolderValue(template);
        if (result.contains(placeHolderValue)) {
            if (result.size() == 1) {
                return Sets.newHashSet();
            } else {
                result.remove(placeHolderValue);
                template.opsForZSet().remove(key, placeHolderValue);
            }
        }
        return result;
    }


    /**
     * 批量从zset中删除元素。参数del为null或为空, 直接返回,不作任何操作
     *
     * @param template
     * @param key
     * @param del
     * @param <K>
     * @param <T>
     */
    public <K, T> void mutDeleteFromZSet(RedisTemplate<K, T> template, K key, List<T> del) {
        if (del != null && del.size() > 0) {
            try {
                template.opsForZSet().remove(key, del.toArray());
            } catch (Exception e) {
                logger.error("cache exception when mutDeleteFromZSet", e);
            }
        }
    }

    public <K, T> void deleteFromZSet(RedisTemplate<K, T> template, K key, T target) {
        List<T> ts = Lists.newArrayList(target);
        mutDeleteFromZSet(template, key, ts);
    }

    /**
     * 获取元素在zset中的索引号。根据score倒排。
     * 返回-1表示不存在
     *
     * @param template
     * @param key
     * @param target
     * @param <K>
     * @param <T>
     *
     * @return
     */
    public <K, T> Long getRankFromZSet(RedisTemplate<K, T> template, K key, T target) {
        try {
            Long offset = template.opsForZSet().reverseRank(key, target);
            if (offset == null) {
                return -1L; //
            }
            return offset;
        } catch (Exception e) {
            logger.error("cache exception when getRankFromZSet", e);
            return -1L;
        }
    }

    public <K, T> boolean isMember(RedisTemplate<K, T> template, K key, T target) {
        try {
            Long rank = template.opsForZSet().rank(key, target);
            return rank != null;
        } catch (Exception e) {
            logger.error("cache exception when getRankFromZSet", e);
            return false;
        }
    }


    public <K, T> void add2ZSet(RedisTemplate<K, T> template, K key, T candidate, double score) {
        add2ZSet(template, key, candidate, score, null);
    }

    public <K, T> void add2ZSet(RedisTemplate<K, T> template, K key, T candidate, double score, Long ttl) {
        try {
            if (candidate != null) {
                if (ttl != null) {
                    template.expire(key, ttl, TimeUnit.SECONDS);
                }
                template.opsForZSet().add(key, candidate, score);
                T placeHolderValue = CacheUtil.getPlaceHolderValue(template);
                template.opsForZSet().remove(key, placeHolderValue); //删除占位元素
            }
        } catch (Exception e) {
            logger.error("cache exception when add2ZSet", e);
        }
    }

    public <K, T> Long getCountOfZSet(RedisTemplate<K, T> template, K key) {
        try {
            if (!template.hasKey(key)) {
                return null;
            }
            Long count = template.opsForZSet().size(key);
            if (count != null && count == 1) {
                if (template.opsForZSet().rank(key, Objects.requireNonNull(CacheUtil.getPlaceHolderValue(template))) != null) {
                    return 0L;
                }
            }
            return count;
        } catch (Exception e) {
            logger.error("cache exception when getCountOfZSet", e);
            return 0L;
        }

    }

    public <K, V> Double getScore(RedisTemplate<K, V> template, K key, V value) {
        return template.hasKey(key) ? template.opsForZSet().score(key, value) : null;
    }
}
