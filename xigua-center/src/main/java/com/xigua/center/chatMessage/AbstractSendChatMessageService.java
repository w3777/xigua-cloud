package com.xigua.center.chatMessage;

import com.xigua.api.service.ChatMessageService;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.ws.MessageRequest;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.enums.ChatType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName AbstractChatMessageService
 * @Description 聊天消息 抽象类
 * @Author wangjinfei
 * @Date 2025/8/17 15:45
 */
public abstract class AbstractSendChatMessageService {
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
     * @param messageRequest
     * @return Boolean
    */
    public abstract Boolean saveMessageUnread(MessageRequest messageRequest);

    /**
     * 最后一条消息  (消息列表)
     * @author wangjinfei
     * @date 2025/8/17 17:09
     * @param messageRequest
    */
    public abstract void lastMessage(MessageRequest messageRequest);

    /**
     * 聊天消息发送到接收人
     * @author wangjinfei
     * @date 2025/8/17 17:14
     * @param messageRequest
    */
    public abstract void chatMessage2Receiver(MessageRequest messageRequest);

    /**
     * 未读消息数量
     * @author wangjinfei
     * @date 2025/8/17 17:25
     * @param messageRequest
    */
    public abstract void unreadMessageCount(MessageRequest messageRequest);

    /**
     * 消息未读到消息已读
     * @author wangjinfei
     * @date 2025/8/17 16:33
     * @param messageRequest
     */
    public abstract void unread2Read(MessageRequest messageRequest);

    /**
     * 保存聊天消息
     * @author wangjinfei
     * @date 2025/8/17 15:47
     * @param messageRequest
     * @return Boolean
    */
    public Boolean saveChatMessage(MessageRequest messageRequest){
        // 封装聊天消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(messageRequest.getChatMessageId());
        chatMessage.setSenderId(messageRequest.getSenderId());
        chatMessage.setReceiverId(messageRequest.getReceiverId());
        chatMessage.setMessage(messageRequest.getMessage());
        chatMessage.setChatType(messageRequest.getChatType());
        // ws拿不到threadLocal存储的当前用户 （此时的发送人就是创建人）
        chatMessage.setCreateBy(messageRequest.getSenderId());
        // 添加聊天消息
        boolean save = chatMessageService.save(chatMessage);

        return save;
    }

}
