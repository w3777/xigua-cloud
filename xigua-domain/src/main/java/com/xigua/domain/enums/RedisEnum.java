package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum RedisEnum {
    CLIENT_CONNECT_CENTER("client_connect_center:", "客户端长连接映射信息"),
    ONLINE_USER("online_user:", "在线用户"),
    EMAIL_CODE("email_code:", "邮箱验证码"),
    LOCATION_IP("location_ip:", "ip位置信息"),
    WEATHER_CITY("weather_city:", "城市天气信息"),
    LAST_MES("last_mes:", "最后消息"),
    LAST_MES_CONTENT("last_mes_content:", "好友最后的消息"),
    USER("user:", "用户"),
    FRIEND_RELATION("friend_relation:", "好友关系"),
    CURRENT_CHAT_WINDOW("current_chat_window:", "当前聊天窗口"),
    FRIEND_UNREAD_COUNT("friend_unread_count:", "好友未读数量"),
    GROUP_UNREAD_COUNT("group_unread_count:", "群聊未读数量"),
    TICKET("ticket:", "一次性ticket"),
    LAST_PING_TIME("last_ping_time:", "最后心跳时间"),
    GROUP("group:", "群聊信息"),
    GROUP_MEMBER("group_member:", "群成员"),
    GROUP_MEMBER_ID("group_member_id:", "群成员id"),
    BOT("bot:", "机器人"),
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
