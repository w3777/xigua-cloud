package com.xigua.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName GroupVO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/27 11:42
 */
@Data
public class GroupVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String groupId;
    private String groupName;
    private String groupAvatar;
    private Integer currentMember;
}
