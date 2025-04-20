package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum RedisEnum {
    BIG_KEY_PREFIX("big_key:", "大key前缀"),
    CLIENT_CONNECT_SERVER("client_connect_server:", "客户端长连接映射信息"),
    ONLINE_USER("online_user:", "在线用户"),
    ;

    final String key;
    final String desc;

    public String getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }

    public static RedisEnum getByKey(String key) {
        for (RedisEnum redisEnum : RedisEnum.values()) {
            if (redisEnum.getKey() == key) {
                return redisEnum;
            }
        }
        return null;
    }

    public static RedisEnum getByDesc(String desc) {
        for (RedisEnum redisEnum : RedisEnum.values()) {
            if (redisEnum.getDesc().equals(desc)) {
                return redisEnum;
            }
        }
        return null;
    }
}
