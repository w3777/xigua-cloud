package com.xigua.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName GroupMember
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/5 17:15
 */
@Data
@TableName("xg_group_member")
@Schema(title = "群组成员实体")
public class GroupMember implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Schema(name = "主键id")
    @TableId(value = "id")
    private String id;

    /**
     * 群组id
     */
    @Schema(name = "群组id")
    private String groupId;

    /**
     * 用户id
     */
    @Schema(name = "用户id")
    private String userId;

    /**
     * 群内昵称
     */
    @Schema(name = "群内昵称")
    private String nickname;

    /**
     * 状态（0：已退出；1：正常）
     */
    @Schema(name = "状态（0：已退出；1：正常）")
    private Integer status;

    /**
     * 群内角色（1：群主；2：群管理；3：普通成员）
     */
    @Schema(name = "群内角色（1：群主；2：群管理；3：普通成员）")
    private Integer role;

    /**
     * 是否删除 （0：未删除；1：已删除）
     */
    @Schema(name = "是否删除 （0：未删除；1：已删除）")
    @TableLogic
    private Integer delFlag;

    /**
     * 创建人
     */
    @Schema(name = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @Schema(name = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    @Schema(name = "修改人")
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    /**
     * 修改时间
     */
    @Schema(name = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
