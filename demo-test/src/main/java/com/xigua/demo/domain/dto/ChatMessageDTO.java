package com.xigua.demo.domain.dto;

import lombok.Data;

/**
 * @ClassName MessageBaseDTO
 * @Description
 * @Author wangjinfei
 * @Date 2024/12/2 16:28
 */
@Data
public class ChatMessageDTO {
    /**
     * 发送人id
     */
    private String senderId;

    /**
     * 接收人id
     */
    private String receiverId;

    /**
     * 消息文本正文
     */
    private String message;
}
