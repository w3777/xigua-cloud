package com.xigua.common.core.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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
}
