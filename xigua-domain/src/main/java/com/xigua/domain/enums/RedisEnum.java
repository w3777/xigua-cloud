package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum RedisEnum {
    BIG_KEY_PREFIX("big_key:", "大key前缀"),
    CLIENT_CONNECT_CENTER("client_connect_center:", "客户端长连接映射信息"),
    ONLINE_USER("online_user:", "在线用户"),
    EMAIL_CODE("email_code:", "邮箱验证码"),
    LOCATION_IP("location_ip:", "ip位置信息"),
    WEATHER_CITY("weather_city:", "城市天气信息"),
    LAST_MES_FRIEND("last_mes_friend:", "最后消息好友"),
    LAST_MES("last_mes:", "好友最后的消息"),
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
