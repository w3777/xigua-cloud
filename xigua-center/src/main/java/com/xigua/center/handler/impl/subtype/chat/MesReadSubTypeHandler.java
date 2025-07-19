package com.xigua.center.handler.impl.subtype.chat;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xigua.center.handler.base.SubTypeHandler;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.connect.Client;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.enums.*;
import com.xigua.service.CenterService;
import com.xigua.service.ChatMessageService;
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
public class MesReadSubTypeHandler implements SubTypeHandler {
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CenterService centerService;

    @Override
    public String getSubType() {
        return MessageSubType.MES_READ.getType();
    }

    @Override
    public void handle(ChatMessageDTO chatMessageDTO) {
        String senderId = chatMessageDTO.getSenderId();
        String message = chatMessageDTO.getMessage();
        if(StringUtils.isEmpty(message)){
            return;
        }
        List<String> chatMessageIdList = JSONArray.parseArray(message, String.class);
        if(CollectionUtils.isEmpty(chatMessageIdList)){
            return;
        }
        // 批量更新消息为已读
        chatMessageService.batchRead(chatMessageIdList, senderId);

        // 接收人处理  todo 可以优化成异步处理，减少阻塞
        receiverHande(chatMessageDTO);

        // 好友未读消息清零
        friendUnreadHande(chatMessageDTO);
    }

    @Override
    public String getMessageType() {
        return MessageType.CHAT.getType();
    }

    /**
     * 接收人处理 （要给消息发送者发送已读）
     * @author wangjinfei
     * @date 2025/6/6 21:51
     * @param chatMessageDTO
    */
    private void receiverHande(ChatMessageDTO chatMessageDTO){
        String senderId = chatMessageDTO.getSenderId();
        String receiverId = chatMessageDTO.getReceiverId();

        String userInServer = centerService.onlineUser(receiverId);
        if(StringUtils.isEmpty(userInServer)){
            return;
        }

        // 获取接收人打开的聊天窗口好友是谁
        String receiverActiveFriend = redisUtil.get(RedisEnum.CURRENT_CHAT_WINDOW.getKey() + receiverId);
        if(StringUtils.isEmpty(receiverActiveFriend)){
            return;
        }

        // 接收人打开的聊天窗口好友是发送人，发送已读通知  （对方在线 && 对方打开的聊天框是发送人）
        if(receiverActiveFriend.equals(senderId)){
            // 获取发送人所在的节点信息
            String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                    userInServer.split(":")[1] + ":" + userInServer.split(":")[2];
            String value = redisUtil.get(key);
            Client client = JSONObject.parseObject(value, Client.class);

            // 系统推送已读通知
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setSenderId(Sender.SYSTEM.getSender());
            dto.setReceiverId(receiverId);
            dto.setMessageType(MessageType.CHAT.getType());
            dto.setSubType(MessageSubType.MES_READ.getType());
            String json = JSONObject.of("readChatMessageIds", chatMessageDTO.getMessage().toString()).toJSONString();
            dto.setMessage(json);
            dto.setCreateTime(DateUtil.formatDateTime(LocalDateTime.now(), DateUtil.DATE_TIME_FORMATTER));
            centerService.sendMessage2Client(dto, client);


            /**
             * 如果要做群聊已读，需要拆出一张已读表，来做一对多的关系
            */
            // 修改消息状态为已读
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setId(chatMessageDTO.getChatMessageId());
            chatMessage.setIsRead(ChatMessageIsRead.READ.getType());
            chatMessage.setReadTime(LocalDateTime.now());
            chatMessage.setUpdateBy(receiverId);
            chatMessage.setUpdateTime(LocalDateTime.now());
            chatMessageService.updateById(chatMessage);
        }
    }

    /**
     * 好友未读消息清零
     * @author wangjinfei
     * @date 2025/6/9 21:36
     * @param chatMessageDTO
    */
    private void friendUnreadHande(ChatMessageDTO chatMessageDTO){
        String senderId = chatMessageDTO.getSenderId();
        String receiverId = chatMessageDTO.getReceiverId();
        String friendUnreadCountKey = RedisEnum.FRIEND_UNREAD_COUNT.getKey() + senderId;

        /**
         * todo 点开聊天框，清零，可以根据已读数据逐次 - 1
         * 暂时直接清零
        */
        redisUtil.hashPut(friendUnreadCountKey, receiverId, 0);
    }
}
