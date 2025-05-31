package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum MessageSubType {
    FRIEND_ONLINE("friend_online", "好友上线通知"),
    FRIEND_OFFLINE("friend_offline", "好友下线通知"),
    ;

    final String type;

    final String desc;

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
