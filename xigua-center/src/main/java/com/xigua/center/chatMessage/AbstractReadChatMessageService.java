package com.xigua.center.chatMessage;

import com.alibaba.fastjson2.JSONArray;
import com.xigua.api.service.MessageReadService;
import com.xigua.domain.enums.ChatType;
import com.xigua.domain.ws.MessageRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName AbstractReadChatMessageService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/24 11:22
 */
public abstract class AbstractReadChatMessageService {
    @Autowired
    private MessageReadService messageReadService;

    /**
     * 获取聊天类型
     * @author wangjinfei
     * @date 2025/8/17 16:16
     * @return ChatType
     */
    protected abstract ChatType getChatType();

    /**
     * 通知发送人消息已读
     * @author wangjinfei
     * @date 2025/8/24 11:32
     * @param messageRequest
    */
    public abstract void notifySenderOfMessageRead(MessageRequest messageRequest);

    /**
     * 清空未读消息数量
     * @author wangjinfei
     * @date 2025/8/24 11:35
     * @param messageRequest
    */
    public abstract void clearUnreadCount(MessageRequest messageRequest);

    /**
     * 批量标记已读
     * @author wangjinfei
     * @date 2025/8/24 11:28
     * @param messageRequest
     */
    public void markReadBatch(MessageRequest messageRequest){
        String senderId = messageRequest.getSenderId();
        String message = messageRequest.getMessage();
        List<String> chatMessageIdList = JSONArray.parseArray(message, String.class);
        // 批量更新消息为已读
        messageReadService.markReadBatch(chatMessageIdList, senderId);
    }
}
