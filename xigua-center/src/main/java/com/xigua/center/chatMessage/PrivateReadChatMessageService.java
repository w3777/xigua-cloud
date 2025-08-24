package com.xigua.center.chatMessage;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.api.service.CenterService;
import com.xigua.api.service.MessageReadService;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.connect.Client;
import com.xigua.domain.enums.*;
import com.xigua.domain.ws.MessageRequest;
import com.xigua.domain.ws.MessageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @ClassName PrivateReadChatMessageService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/24 11:41
 */
@Component
public class PrivateReadChatMessageService extends AbstractReadChatMessageService{
    @Autowired
    private MessageReadService messageReadService;
    @Autowired
    private CenterService centerService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected ChatType getChatType() {
        return ChatType.ONE;
    }

    /**
     * 通知发送人消息已读
     * @author wangjinfei
     * @date 2025/8/24 11:32
     * @param messageRequest
     */
    @Override
    public void notifySenderOfMessageRead(MessageRequest messageRequest) {
        String senderId = messageRequest.getSenderId();
        String receiverId = messageRequest.getReceiverId();

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
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setSenderId(Sender.SYSTEM.getSender());
            messageResponse.setReceiverId(receiverId);
            messageResponse.setMessageType(MessageType.CHAT.getType());
            messageResponse.setSubType(MessageSubType.MES_READ.getType());
            String json = JSONObject.of("readChatMessageIds", messageRequest.getMessage().toString()).toJSONString();
            messageResponse.setMessage(json);
            messageResponse.setCreateTime(DateUtil.formatDateTime(LocalDateTime.now(), DateUtil.DATE_TIME_FORMATTER));
            centerService.sendMessage2Client(messageResponse, client);
        }
    }

    /**
     * 清空未读消息数量
     * @author wangjinfei
     * @date 2025/8/24 11:35
     * @param messageRequest
     */
    @Override
    public void clearUnreadCount(MessageRequest messageRequest) {
        String senderId = messageRequest.getSenderId();
        String receiverId = messageRequest.getReceiverId();
        String friendUnreadCountKey = RedisEnum.FRIEND_UNREAD_COUNT.getKey() + senderId;

        /**
         * todo 点开聊天框，清零，可以根据已读数据逐次 - 1
         * 暂时直接清零
         */
        redisUtil.hashPut(friendUnreadCountKey, receiverId, 0);
    }
}
