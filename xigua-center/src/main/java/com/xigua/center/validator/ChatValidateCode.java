package com.xigua.center.validator;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum ChatValidateCode {
    SUCCESS(0, "成功"),
    NOT_FRIEND(10001, "你们不是好友关系，无法发送消息"),
    ;
    private final Integer code;
    private final String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
