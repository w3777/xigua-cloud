package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum MessageType {
    CHAT("chat","聊天消息"),
    NOTIFY("notify","通知消息"),
    UNREAD("unread", "未读消息")
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
