package com.xigua.common.core.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName RedisUtil
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/18 16:14
 */
@Component
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
}
