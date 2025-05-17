package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum FriendRequestFlowStatus {
    ZERO(0,"待处理"),
    ONE(1,"同意"),
    TWO(2,"拒绝");

    final Integer status;

    final String desc;

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
