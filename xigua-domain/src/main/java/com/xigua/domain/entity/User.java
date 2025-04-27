package com.xigua.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName User
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/27 8:38
 */
@Data
@TableName("xg_user")
@Schema(title = "用户实体")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @Schema(name = "主键id")
    @TableId(value = "id")
    private String id;

    /**
     * 用户名
     */
    @Schema(name = "用户名")
    private String username;

    /**
     * 密码
     */
    @Schema(name = "密码")
    private String password;

    /**
     * 昵称
     */
    @Schema(name = "昵称")
    private String nickname;

    /**
     * 邮箱
     */
    @Schema(name = "邮箱")
    private String email;

    /**
     * 手机号
     */
    @Schema(name = "手机号")
    private String phone;

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
