package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum ChatType {
    ONE(1,"privateChat","单聊"),
    TWO(2,"groupChat","群聊"),
    THREE(3,"aiChat","ai对话")
    ;

    final Integer type;

    final String name;

    final String desc;

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }


    public static ChatType getChatType(Integer type) {
        for (ChatType chatType : ChatType.values()) {
            if(chatType.type.equals(type)){
                return chatType;
            }
        }
        return null;
    }
}
