package com.xigua.center.wsMessage;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xigua.api.service.MessageReadService;
import com.xigua.center.chatMessage.AbstractReadChatMessageService;
import com.xigua.center.chatMessage.ReadChatMessageServiceFactory;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.connect.Client;
import com.xigua.domain.ws.MessageRequest;
import com.xigua.domain.enums.*;
import com.xigua.api.service.CenterService;
import com.xigua.api.service.ChatMessageService;
import com.xigua.domain.ws.MessageResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName MesReadSubTypeHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/4 21:50
 */
@Component
public class ReadChatMessageService extends AbstractMessageService {
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CenterService centerService;
    @Autowired
    private MessageReadService messageReadService;

    @Override
    public String getMessageName() {
        return MessageSubType.SUBMIT_UNREAD.getDesc();
    }

    @Override
    public String getMessageType() {
        return MessageType.CHAT.getType();
    }

    @Override
    public String getMessageSubType() {
        return MessageSubType.SUBMIT_UNREAD.getType();
    }

    @Override
    public void handleMessage(MessageRequest messageRequest) {
        String senderId = messageRequest.getSenderId();
        String message = messageRequest.getMessage();
        if(StringUtils.isEmpty(message)){
            return;
        }
        List<String> chatMessageIdList = JSONArray.parseArray(message, String.class);
        if(CollectionUtils.isEmpty(chatMessageIdList)){
            return;
        }
        // 批量更新消息为已读
        markReadBatch(messageRequest);

        // 通知发送人消息已读  todo 可以优化成异步处理，减少阻塞
        notifySenderOfMessageRead(messageRequest);

        // 清空未读消息数量
        clearUnreadCount(messageRequest);
    }

    /**
     * 批量标记已读
     * @author wangjinfei
     * @date 2025/8/24 17:06
     * @param messageRequest
    */
    private void markReadBatch(MessageRequest messageRequest){
        ChatType chatTypeE = ChatType.getChatType(messageRequest.getChatType());
        AbstractReadChatMessageService readService = ReadChatMessageServiceFactory.getReadService(chatTypeE.getName());
        readService.markReadBatch(messageRequest);
    }

    /**
     * 通知发送人消息已读
     * @author wangjinfei
     * @date 2025/6/6 21:51
     * @param messageRequest
    */
    private void notifySenderOfMessageRead(MessageRequest messageRequest){
        ChatType chatTypeE = ChatType.getChatType(messageRequest.getChatType());
        AbstractReadChatMessageService readService = ReadChatMessageServiceFactory.getReadService(chatTypeE.getName());
        readService.notifySenderOfMessageRead(messageRequest);
    }

    /**
     * 清空未读消息数量
     * @author wangjinfei
     * @date 2025/6/9 21:36
     * @param messageRequest
    */
    private void clearUnreadCount(MessageRequest messageRequest){
        ChatType chatTypeE = ChatType.getChatType(messageRequest.getChatType());
        AbstractReadChatMessageService readService = ReadChatMessageServiceFactory.getReadService(chatTypeE.getName());
        readService.clearUnreadCount(messageRequest);
    }
}
