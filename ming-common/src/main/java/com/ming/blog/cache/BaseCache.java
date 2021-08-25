package com.ming.blog.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author unknown
 */
@Slf4j
public abstract class BaseCache<K, V> {

    private static Map<String, RedisTemplate> redisTemplateMap = new HashMap<>();

    @Autowired(required = false)
    private RedisConnectionFactory jedisConnectionFactory;

    @PostConstruct
    private void setRedisTemplate() {
        if (jedisConnectionFactory == null) {
            log.info("RedisConnectionFactory not found . do not using cache.");
            return;
        }
        ParameterizedType parameterizedType = (ParameterizedType) (this.getClass().getGenericSuperclass());
        Type[] types = parameterizedType.getActualTypeArguments();

        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        if (types.length == 2) {
            Type vType = types[1];
            try {
                Class clazz = Class.forName(vType.getTypeName());
                if (vType.getTypeName().equals(String.class.getTypeName())) {
                    redisTemplate.setValueSerializer(new StringRedisSerializer());
                } else {
                    redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(clazz));
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("请指定泛型DTO");
            }
        } else if (types.length == 3) { //Hash类型的会有三个泛型参数
            Type hkType = types[1];
            try {
                Class clazz = Class.forName(hkType.getTypeName());
                if (hkType.getTypeName().equals(String.class.getTypeName())) {
                    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
                } else {
                    redisTemplate.setHashKeySerializer(new FastJsonRedisSerializer<>(clazz));
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("请指定泛型DTO");
            }
            Type vType = types[2];
            try {
                Class clazz = Class.forName(vType.getTypeName());
                if (vType.getTypeName().equals(String.class.getTypeName())) {
                    redisTemplate.setHashValueSerializer(new StringRedisSerializer());
                    redisTemplate.setValueSerializer(new StringRedisSerializer());
                } else {
                    redisTemplate.setHashValueSerializer(new FastJsonRedisSerializer<>(clazz));
                    redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(clazz));
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("请指定泛型DTO");
            }
        } else {
            throw new RuntimeException("请指定泛型DTO");
        }

        redisTemplate.afterPropertiesSet();
        redisTemplateMap.put(this.getClass().getName(), redisTemplate);
    }

    protected RedisTemplate<String, V> getRedisTemplate() {
        return redisTemplateMap.get(this.getClass().getName());
    }

    protected abstract String getPrefix();

    protected String buildKey(K key) {
        String prefix = getPrefix();
        if (prefix == null) {
            throw new RuntimeException("缓存前缀不能为NULL");
        }
        if (key == null || StringUtils.isBlank(key.toString())) {
            return prefix;
        }
        return prefix + key.toString();
    }

    protected Long getTTL() {
        return 30 * 24 * 3600L;
    }

    public void drop(K key) {
        RedisTemplate<String, V> redisTemplate = getRedisTemplate();
        String cacheKey = buildKey(key);
        CacheUtil.drop(redisTemplate, cacheKey);
    }

    public void drop() {
        drop(null);
    }

    public void mutDrop(List<K> ids) {
        List<String> keys = ids.stream().map(this::buildKey).collect(Collectors.toList());
        CacheUtil.mutDrop(getRedisTemplate(), keys);
    }

    public boolean exist(K key) {
        String cacheKey = buildKey(key);
        return CacheUtil.hasKey(getRedisTemplate(), cacheKey);
    }

    public boolean exist() {
        String cacheKey = buildKey(null);
        return CacheUtil.hasKey(getRedisTemplate(), cacheKey);
    }

}

