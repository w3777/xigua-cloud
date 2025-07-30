package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum GroupRole {
    OWNER(1, "群主"),
    GROUP_ADMIN(2, "群管理"),
    MEMBER(3, "普通成员"),
    ;

    final Integer type;
    final String desc;

    public static GroupRole getByType(Integer type) {
        for (GroupRole groupRole : GroupRole.values()) {
            if (groupRole.type.equals(type)) {
                return groupRole;
            }
        }
        return null;
    }
}
