package com.xigua.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MessageBaseDTO
 * @Description
 * @Author wangjinfei
 * @Date 2024/12/2 16:28
 */
@Data
public class ChatMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 发送人id
     */
    private String senderId;

    /**
     * 接收人id
     */
    private String receiverId;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 消息文本正文
     */
    private String message;
}
