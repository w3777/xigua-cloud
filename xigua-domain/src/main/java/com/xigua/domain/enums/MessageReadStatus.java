package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum MessageReadStatus {
    // 是否已读（0：未读；1：已读）
    UNREAD(0,"未读"),
    READ(1,"已读"),
    ;

    final Integer type;

    final String desc;

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
