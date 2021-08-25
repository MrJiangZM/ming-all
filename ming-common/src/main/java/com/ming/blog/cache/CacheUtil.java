package com.ming.blog.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author unknown
 */
public class CacheUtil {

    private static Logger logger = LoggerFactory.getLogger(CacheUtil.class);

    private static ZSetUtil zSetUtil = new ZSetUtil();
    private static ValueUtil valueUtil = new ValueUtil();
    private static ListUtil listUtil = new ListUtil();
    private static HashUtil hashUtil = new HashUtil();
    private static SetUtil setUtil = new SetUtil();

    public static ZSetUtil zset() {
        return zSetUtil;
    }

    public static ValueUtil val() {
        return valueUtil;
    }

    public static ListUtil list() {
        return listUtil;
    }

    public static HashUtil hash() {
        return hashUtil;
    }

    public static SetUtil set() {
        return setUtil;
    }

    public static <K, T> void drop(RedisTemplate<K, T> template, K key) {
        try {
            template.delete(key);
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
    }

    public static <K, T> void mutDrop(RedisTemplate<K, T> template, Collection<K> key) {
        try {
            template.delete(key);
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
    }

    public static <K, T> boolean hasKey(RedisTemplate<K, T> template, K key) {
        try {
            return template.hasKey(key);
        } catch (Exception e) {
            logger.error("cache exception", e);
            return false;
        }
    }

    public static <K, T> void expireSec(RedisTemplate<K, T> template, K key, long ttl) {
        try {
            template.expire(key, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("cache exception", e);
        }
    }

    public static <K, T> boolean isPlaceHolder(RedisTemplate<K, T> template, T value) {
        T placeHolder = getPlaceHolderValue(template);
        if (value instanceof BaseCo) { //只支持BaseCo和基本Long的占位符
            if (placeHolder instanceof BaseCo) {
                return ((BaseCo) value).isNullValue();
            }
            return false;
        } else if (value == null) {
            return true;
        } else {
            return value.equals(placeHolder);
        }
    }

    public static <K, T> T getPlaceHolderValue(RedisTemplate<K, T> template) {
        RedisSerializer serializer = template.getValueSerializer();
        if (serializer instanceof FastJsonRedisSerializer) {
            Class<T> clazz = ((FastJsonRedisSerializer) serializer).getType();
            if (clazz.equals(Long.class)) {
                return (T) Long.valueOf(-1);
            } else if (BaseCo.class.isAssignableFrom(clazz)) {
                try {
                    T t = clazz.newInstance();
                    ((BaseCo) t).setNullValue(true);
                    return t;
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(clazz.getName() + "必须有public的无参构造函数");
                }
            } else {
                return null;
//                throw new RuntimeException("unsupported type. 目前只支持Long, 可以到CacheUtil中增加需要支持的类型");
            }
        } else if (serializer instanceof StringRedisSerializer) {
            return (T) "-1";
        } else {
            throw new RuntimeException("unsupported serializer. 只支持FastJsonRedisSerializer和StringRedisSerializer");
        }
    }

    protected static <K, T, HK> HK getHashPlaceHolderKey(RedisTemplate<K, T> template) {
        RedisSerializer serializer = template.getHashKeySerializer();
        if (serializer instanceof FastJsonRedisSerializer) {
            Class<HK> clazz = ((FastJsonRedisSerializer) serializer).getType();
            if (clazz.equals(Long.class)) {
                return (HK) Long.valueOf(-1);
            } else {
                throw new RuntimeException("unsupported type. 目前只支持Long和BaseCo的子类, 可以到CacheUtil中增加需要支持的类型");
            }
        } else if (serializer instanceof StringRedisSerializer) {
            return (HK) "-1";
        } else {
            throw new RuntimeException("unsupported serializer. 只支持FastJsonRedisSerializer和StringRedisSerializer");
        }
    }
}

