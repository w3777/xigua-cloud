package com.xigua.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName GroupDetailVO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/30 19:29
 */
@Data
public class GroupDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 群组id
     */
    @Schema(name = "群组id")
    private String groupId;

    /**
     * 群组名称
     */
    @Schema(name = "群组名称")
    private String groupName;

    /**
     * 群组头像
     */
    @Schema(name = "群组头像")
    private String groupAvatar;

    /**
     * 群组描述
     */
    @Schema(name = "群组描述")
    private String description;

    /**
     * 当前成员数
     */
    @Schema(name = "当前成员数")
    private Integer currentMember;

    /**
     * 创建时间
     */
    @Schema(name = "创建时间")
    private String createTime;

    /**
     * 群主
     */
    @Schema(name = "群主")
    private String groupOwner;

    /**
     * 我的角色
     */
    @Schema(name = "我的角色")
    private String roleName;

    /**
     * 加入时间
     */
    @Schema(name = "加入时间")
    private String joinTime;
}
