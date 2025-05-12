package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum UserConnectStatus {
    ONLINE(1,"在线"),
    OFFLINE(2,"离线");

    final Integer status;

    final String desc;

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
