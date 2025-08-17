package com.xigua.common.core.util;

import com.alibaba.fastjson2.JSON;
import com.xigua.common.core.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName RedisUtil
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/18 16:14
 */
@Component
@ConditionalOnBean({RedisConfig.class})
@RequiredArgsConstructor
public class RedisUtil {
    public final RedisTemplate<String, Object> redisTemplate;

    /**
     * set集合添加缓存
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    public boolean sadd(String key, Object value) {
        try {
            redisTemplate.opsForSet().add(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * set集合 key获取所有value
     * @author wangjinfei
     * @date 2025/4/23 21:59
     * @param key
     * @return Set<Object>
    */
    public Set<Object> members (String key) {
        Set<Object> members = redisTemplate.opsForSet().members(key);
        return members;
    }

    /**
     * 获取set集合长度
     * @author wangjinfei
     * @date 2025/4/18 17:19
     * @param key
     * @return Long
    */
    public Long scard(String key) {
        Long size = redisTemplate.opsForSet().size(key);
        return size;
    }

    /**
     * set集合 根据key删除value
     * @author wangjinfei
     * @date 2025/4/24 15:45
     * @param key
     * @param value
     * @return boolean
    */
    public boolean srem(String key, Object value) {
        try {
            redisTemplate.opsForSet().remove(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * string添加缓存
     * @author wangjinfei
     * @date 2025/4/20 10:38
     * @param key
     * @param value
     * @return boolean
    */
    public boolean set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * string添加缓存 并设置过期时间
     * @author wangjinfei
     * @date 2025/4/20 10:38
     * @param key
     * @param value
     * @param expire
     * @return boolean
     */
    public boolean set(String key, String value, long expire) {
        try {
            redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * string获取缓存
     * @author wangjinfei
     * @date 2025/4/20 19:15
     * @param key
     * @return Object
    */
    public String get(String key) {
        String o = (String) redisTemplate.opsForValue().get(key);
        return o;
    }

    /**
     * 根据key 删除缓存
     * @author wangjinfei
     * @date 2025/4/24 15:50
     * @param key
     * @return Boolean
    */
    public Boolean del(String key){
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断value是否存在于set集合中
     * @author wangjinfei
     * @date 2025/4/20 11:22
     * @param value value值
     * @param key 多个key用*匹配
     * @return boolean
    */
    public String isValueInSet(String value, String key) {
        // 获取增量迭代器，用于遍历匹配的 Redis 键
        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
                .getConnection()
                .scan(ScanOptions.scanOptions().match(key).build());

        // 遍历 SCAN 结果
        while (cursor.hasNext()) {
            String currentKey = new String(cursor.next());
            if (redisTemplate.opsForSet().isMember(currentKey, value)) {
                return currentKey; // 如果找到用户，说明用户在线
            }
        }

        return null; // 如果没找到该用户，说明不在线
    }

    /**
     * 根据模糊key获取所有key
     * @author wangjinfei
     * @date 2025/4/23 21:59
     * @param key key:*
     * @return Set<String> 所有key
    */
    public Set<String> getKeysByKey(String key) {
        Set<String> keys = new HashSet<>();

        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
                .getConnection()
                .scan(ScanOptions.scanOptions().match(key).build());

        while (cursor.hasNext()) {
            // 获取匹配的 key
            keys.add(new String(cursor.next()));
        }
        return keys;
    }

    /**
     * zset添加缓存
     * @author wangjinfei
     * @date 2025/5/20 20:02
     * @param key
     * @param value
     * @param score
     * @return Boolean
    */
    public Boolean zsadd(String key, Object value, long score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * zset获取指定区间的元素
     * @author wangjinfei
     * @date 2025/5/20 20:54
     * @param key
     * @param start
     * @param end
     * @return Set<Object>
    */
    public Set<Object> zsReverseRange(String key, long start, long end) {
        Set<Object> setObj = redisTemplate.opsForZSet().reverseRange(key, start, end);
        return setObj;
    }

    /**
     * zset获取指定区间的元素
     * @author wangjinfei
     * @date 2025/8/17 16:04
     * @param key
     * @param start
     * @param end
     * @param clazz
     * @return Set<T>
    */
    public <T> Set<T> zsReverseRange(String key, long start, long end, Class<T> clazz) {
        Set<Object> setObj = redisTemplate.opsForZSet().reverseRange(key, start, end);
        if(CollectionUtils.isEmpty(setObj)){
            return Set.of();
        }
        Set<T> set = setObj.stream()
                .map(item -> JSON.parseObject(JSON.toJSONString(item), clazz))
                .collect(Collectors.toSet());

        return set;
    }
    
    /** 
     * zset获取数量
     * @author wangjinfei
     * @date 2025/5/20 21:29 
     * @param key 
    */
    public Long zsetSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 根据 member 扫描 zset 中所有 key
     * @author wangjinfei
     * @date 2025/5/31 10:50
     * @param member 要查询的 member
     * @param pattern key 匹配模式（如 "prefix:*"）
     * @return Set<String>
    */
    public Set<String> scanZSetKeysInMember(String member, String pattern) {
        Set<String> result = new HashSet<>();

        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(100)
                .build();

        redisTemplate.execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    String key = new String(cursor.next(), StandardCharsets.UTF_8);
                    Double score = redisTemplate.opsForZSet().score(key, member);
                    if (score != null) {
                        result.add(key);
                    }
                }
            }
            return null;
        });

        return result;
    }

    /**
     * hash添加换成
     * @author wangjinfei
     * @date 2025/5/20 20:42
     * @param key
     * @param hashKey
     * @param value
     * @return Boolean
    */
    public Boolean hashPut(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /** 
     * hash批量获取key的value
     * @author wangjinfei
     * @date 2025/5/20 21:28 
     * @param key 
     * @param hashKeys 
     * @return List<Object> 
    */
    public List<Object> hashGetRang(String key, Set<Object> hashKeys){
        List<Object> objects = redisTemplate.opsForHash().multiGet(key, hashKeys);
        return objects;
    }

    /**
     * hash获取指定key的value
     * @author wangjinfei
     * @date 2025/5/20 22:37
     * @param key
     * @param hashKey
     * @return Object
    */
    public Object hashGet(String key, String hashKey) {
        Object o = redisTemplate.opsForHash().get(key, hashKey);
        return o;
    }

    /**
     * 自增 Hash 中的字段值（类似 HINCRBY）
     * @author wangjinfei
     * @date 2025/6/15 20:13
     * @param key
     * @param field
     * @param delta
     * @return Long
    */
    public Long hincrby(String key, String field, long delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * 获取 Hash 中字段的值（默认为 Long 类型）
     * @author wangjinfei
     * @date 2025/6/15 20:14
     * @param key
     * @param field
     * @return Long
    */
    public Long hincrget(String key, String field) {
        Object value = redisTemplate.opsForHash().get(key, field);
        return value == null ? 0L : Long.parseLong(value.toString());
    }

    /**
     * 获取 Hash 中所有字段的值并求和（默认为 Long 类型）
     * @author wangjinfei
     * @date 2025/8/9 11:20
     * @param key
     * @return Long
    */
    public Long hincrget(String key){
        List<Object> values = redisTemplate.opsForHash()
                .values(key);
        if(CollectionUtils.isEmpty(values)){
            return 0L;
        }

        long total = values.stream()
                .mapToLong(v -> Long.parseLong(v.toString()))
                .sum();
        return total;
    }

    /**
     * 根据模糊key获取所有set集合中的值
     * @author wangjinfei
     * @date 2025/6/16 20:58
     * @param keyPattern
     * @return Set<String>
    */
    public Set<String> getSetsByPattern(String keyPattern){
        Set<String> values = new HashSet<>();
        Set<String> keys = redisTemplate.keys(keyPattern);
        if(CollectionUtils.isEmpty(keys)){
            return values;
        }

        for (String key : keys) {
            Set<Object> members = redisTemplate.opsForSet().members(key);
            if(CollectionUtils.isNotEmpty(members)){
                Set<String> collect = members.stream().map(Object::toString).collect(Collectors.toSet());
                values.addAll(collect);
            }
        }

        return values;
    }

    /**
     * 获取zset中的score
     * @author wangjinfei
     * @date 2025/7/30 19:53
     * @param key
     * @param member
     * @return long
    */
    public long getScore(String key, String member) {
        Double score = redisTemplate.opsForZSet().score(key, member);
        return score != null ? score.longValue() : 0L;
    }
}
