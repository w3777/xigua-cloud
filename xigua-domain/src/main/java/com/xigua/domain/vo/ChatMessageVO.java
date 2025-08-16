package com.xigua.domain.vo;

import com.xigua.domain.result.BasePageVO;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MessageBaseDTO
 * @Description
 * @Author wangjinfei
 * @Date 2024/12/2 16:28
 */
@Data
public class ChatMessageVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 聊天消息id
     */
    private String chatMessageId;

    /**
     * 发送人id
     */
    private String senderId;

    /**
     * 接收人id
     */
    private String receiverId;

    /**
     * 聊天类型
     */
    private Integer chatType;

    /**
     * 消息文本正文
     */
    private String message;

    /**
     * 是否已读（0：未读；1：已读）
     */
    private Boolean isRead;

    /**
     * 创建时间
     */
    private String createTime;
}
