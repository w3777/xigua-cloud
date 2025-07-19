package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum ChatType {
    ONE(1,"单聊"),
    TWO(2,"群聊"),
    // todo ai对话
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
