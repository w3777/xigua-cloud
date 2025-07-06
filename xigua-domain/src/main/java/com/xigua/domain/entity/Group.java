package com.xigua.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName Group
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/5 17:11
 */
@Data
@TableName("xg_group")
@Schema(title = "群组实体")
public class Group implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Schema(name = "主键id")
    @TableId(value = "id")
    private String id;

    /**
     * 群主id
     */
    @Schema(name = "群主id")
    private String ownerId;

    /**
     * 群名称
     */
    @Schema(name = "群名称")
    private String groupName;

    /**
     * 群头像
     */
    @Schema(name = "群头像")
    private String groupAvatar;

    /**
     * 群描述
     */
    @Schema(name = "群描述")
    private String description;

    /**
     * 当前成员数
     */
    @Schema(name = "当前成员数")
    private Integer currentMember;

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
