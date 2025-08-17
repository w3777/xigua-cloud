package com.xigua.center.chatMessage;

import com.xigua.api.service.ChatMessageService;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.enums.ChatType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName AbstractChatMessageService
 * @Description 聊天消息 抽象类
 * @Author wangjinfei
 * @Date 2025/8/17 15:45
 */
public abstract class AbstractChatMessageService {
    @Autowired
    private Sequence sequence;
    @Autowired
    private ChatMessageService chatMessageService;

    /**
     * 获取聊天类型
     * @author wangjinfei
     * @date 2025/8/17 16:16
     * @return ChatType
    */
    protected abstract ChatType getChatType();

    /**
     * 保存未读消息
     * @author wangjinfei
     * @date 2025/8/17 15:49
     * @param chatMessageDTO
     * @return Boolean
    */
    public abstract Boolean saveMessageUnread(ChatMessageDTO chatMessageDTO);

    /**
     * 最后一条消息  (消息列表)
     * @author wangjinfei
     * @date 2025/8/17 17:09
     * @param chatMessageDTO
    */
    public abstract void lastMessage(ChatMessageDTO chatMessageDTO);

    /**
     * 聊天消息发送到接收人
     * @author wangjinfei
     * @date 2025/8/17 17:14
     * @param chatMessageDTO
    */
    public abstract void chatMessage2Receiver(ChatMessageDTO chatMessageDTO);

    /**
     * 未读消息数量
     * @author wangjinfei
     * @date 2025/8/17 17:25
     * @param chatMessageDTO
    */
    public abstract void unreadMessageCount(ChatMessageDTO chatMessageDTO);

    /**
     * 消息未读到消息已读
     * @author wangjinfei
     * @date 2025/8/17 16:33
     * @param chatMessageDTO
     */
    public abstract void unread2Read(ChatMessageDTO chatMessageDTO);

    /**
     * 保存聊天消息
     * @author wangjinfei
     * @date 2025/8/17 15:47
     * @param chatMessageDTO
     * @return Boolean
    */
    public Boolean saveChatMessage(ChatMessageDTO chatMessageDTO){
        // 封装聊天消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(chatMessageDTO.getChatMessageId());
        chatMessage.setSenderId(chatMessageDTO.getSenderId());
        chatMessage.setReceiverId(chatMessageDTO.getReceiverId());
        chatMessage.setMessage(chatMessageDTO.getMessage());
        chatMessage.setChatType(chatMessageDTO.getChatType());
        // ws拿不到threadLocal存储的当前用户 （此时的发送人就是创建人）
        chatMessage.setCreateBy(chatMessageDTO.getSenderId());
        // 添加聊天消息
        boolean save = chatMessageService.save(chatMessage);

        return save;
    }

}
