package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum MessageSubType {
    // notify
    FRIEND_ONLINE("friend_online", "好友上线通知"),
    FRIEND_OFFLINE("friend_offline", "好友下线通知"),
    SWITCH_CHAT_WINDOW("switch_chat_window", "切换聊天窗口"),
    REMOVE_FRIEND("close_chat_window", "关闭聊天窗口"),


    // chat
    MES_SEND("mes_send", "消息发送"),
    MES_SEND_ACK("mes_send_ack", "消息发送确认"),
    MES_RECEIVE("mes_receive", "消息接收"),
    MES_READ("mes_read", "消息已读"),

    // unread
    FRIEND_UNREAD("friend_unread", "好友未读消息"),

    // heart_beat
    PING("ping", "心跳发送（客户端发送）"),
    PONG("pong", "心跳回复（服务端响应）"),
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
