package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum FriendRequestStatus {
    ZERO(0,"失效"),
    ONE(1,"有效");

    final Integer status;

    final String desc;

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
