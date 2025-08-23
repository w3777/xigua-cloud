package com.xigua.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName Message
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/17 19:12
 */
@Data
@TableName("xg_chat_message")
@Schema(title = "消息实体")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @Schema(name = "主键id")
    @TableId(value = "id")
    private String id;

    /**
     * 发起人
     */
    @Schema(name = "发起人")
    private String senderId;

    /**
     * 接收人
     */
    @Schema(name = "接收人")
    private String receiverId;

    /**
     * 消息内容
     */
    @Schema(name = "消息内容")
    private String message;

    /**
     * 聊天类型（1：单聊；2：群聊）
     */
    @Schema(name = "聊天类型（1：单聊；2：群聊）")
    private Integer chatType;

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
