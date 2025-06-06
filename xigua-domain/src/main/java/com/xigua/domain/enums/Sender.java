package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum Sender {
    SYSTEM("system", "系统发送"),
    ;

    final String sender;
    final String desc;
}
