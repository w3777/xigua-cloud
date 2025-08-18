package com.xigua.domain.dto;

import com.xigua.domain.bo.SenderBO;
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
     * 群聊 真正发送人
    */
    private SenderBO sender;

    /**
     * 接收人id
     */
    private String receiverId;

    /**
     * 消息类型
     * @see com.xigua.domain.enums.MessageType
     */
    private String messageType;

    /**
     * 子类型
     * @see com.xigua.domain.enums.MessageSubType
     */
    private String subType;

    /**
     * 消息文本正文
     */
    private String message;

    /**
     * 聊天类型
     * @see com.xigua.domain.enums.ChatType
     */
    private Integer chatType;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 聊天消息id
     */
    private String chatMessageId;

    /**
     * 服务端收到消息的时间
     */
    private Long serverReceiveTime;
}
