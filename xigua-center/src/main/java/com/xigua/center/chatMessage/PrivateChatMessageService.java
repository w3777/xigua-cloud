package com.xigua.center.chatMessage;


import com.alibaba.fastjson2.JSONObject;
import com.xigua.api.service.CenterService;
import com.xigua.api.service.MessageReadService;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.bo.LastMessageBO;
import com.xigua.domain.bo.LastMessageContentBO;
import com.xigua.domain.connect.Client;
import com.xigua.domain.ws.MessageRequest;
import com.xigua.domain.entity.MessageRead;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.*;
import com.xigua.domain.ws.MessageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @ClassName PrivateChatMessageService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/17 15:50
 */
@Component
public class PrivateChatMessageService extends AbstractChatMessageService {
    @Autowired
    private Sequence sequence;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CenterService centerService;
    @Autowired
    private MessageReadService messageReadService;

    @Override
    protected ChatType getChatType() {
        return ChatType.ONE;
    }

    @Override
    public Boolean saveMessageUnread(MessageRequest messageRequest) {
        // 添加消息未读
        MessageRead messageRead = new MessageRead();
        messageRead.setId(sequence.nextNo());
        messageRead.setMessageId(messageRequest.getChatMessageId());
        messageRead.setSenderId(messageRequest.getSenderId());
        messageRead.setReceiverId(messageRequest.getReceiverId());
        messageRead.setIsRead(MessageReadStatus.UNREAD.getType());
        // ws拿不到threadLocal存储的当前用户 （此时的发送人就是创建人）
        messageRead.setCreateBy(messageRequest.getSenderId());
        boolean save = messageReadService.save(messageRead);
        return save;
    }

    /**
     * 最后一条消息  (消息列表)
     * @author wangjinfei
     * @date 2025/8/17 17:09
     * @param messageRequest
     */
    @Override
    public void lastMessage(MessageRequest messageRequest) {
        String senderId = messageRequest.getSenderId();
        String receiverId = messageRequest.getReceiverId();
        long timestamp = System.currentTimeMillis();

        // 存储redis 最后消息
        redisUtil.zsadd(RedisEnum.LAST_MES.getKey() + receiverId, senderId, timestamp);
        // 存储redis 最后消息内容
        LastMessageBO lastMessageBO = chatMessageDTO2LastMessageBO(messageRequest);
        redisUtil.hashPut(RedisEnum.LAST_MES_CONTENT.getKey() + receiverId, senderId, JSONObject.toJSONString(lastMessageBO));
    }

    /**
     * 封装最后消息内容
     * @author wangjinfei
     * @date 2025/7/18 13:46
     * @param messageRequest
     * @return LastMessageBO
     */
    private LastMessageBO chatMessageDTO2LastMessageBO(MessageRequest messageRequest){
        LastMessageBO lastMessageBO = new LastMessageBO();
        LastMessageContentBO lastMessageContent = new LastMessageContentBO();
        String senderId = messageRequest.getSenderId();

        lastMessageBO.setChatId(senderId);
        lastMessageBO.setChatType(ChatType.ONE.getType());
        lastMessageBO.setUpdateTime(System.currentTimeMillis());

        // 用户信息
        String userCache = redisUtil.get(RedisEnum.USER.getKey() + senderId);
        if(StringUtils.isNotEmpty(userCache)){
            User user = JSONObject.parseObject(userCache, User.class);
            lastMessageBO.setChatName(user.getUsername());
            lastMessageBO.setAvatar(user.getAvatar());
        }else{
            // 从数据库获取用户信息
            lastMessageBO.setAvatar("");
            lastMessageBO.setChatName("");
        }

        // 封装 最后消息内容
        lastMessageContent.setContent(messageRequest.getMessage());
        lastMessageBO.setLastMessageContent(lastMessageContent);

        return lastMessageBO;
    }

    /**
     * 聊天消息发送到接收人
     * @author wangjinfei
     * @date 2025/8/17 17:14
     * @param messageRequest
     */
    @Override
    public void chatMessage2Receiver(MessageRequest messageRequest) {
        // 获取接收人所在的节点信息
        String receiverInServer = centerService.onlineUser(messageRequest.getReceiverId());
        if(StringUtils.isEmpty(receiverInServer)){
            // 如果接收人不在线，直接返回，不做后续处理
            return;
        }
        // 获取接收者所在的节点信息
        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                receiverInServer.split(":")[1] + ":" + receiverInServer.split(":")[2];
        String value = redisUtil.get(key);
        Client client = JSONObject.parseObject(value, Client.class);

        // 实时推送消息，发送到接收人所在节点
        MessageResponse messageResponse = new MessageResponse();
        BeanUtils.copyProperties(messageRequest, messageResponse);
        messageResponse.setSubType(MessageSubType.MES_RECEIVE.getType());
        centerService.sendMessage2Client(messageResponse, client);
    }

    /**
     * 未读消息数量
     * @author wangjinfei
     * @date 2025/8/17 17:25
     * @param messageRequest
     */
    @Override
    public void unreadMessageCount(MessageRequest messageRequest) {
        String senderId = messageRequest.getSenderId();
        String receiverId = messageRequest.getReceiverId();

        // 获取接收人所在的节点信息
        String receiverInServer = centerService.onlineUser(messageRequest.getReceiverId());
        if(StringUtils.isEmpty(receiverInServer)){
            return;
        }

        // 获取接收人打开的聊天窗口好友是谁
        String receiverActiveFriend = redisUtil.get(RedisEnum.CURRENT_CHAT_WINDOW.getKey() + receiverId);

        // 存储redis 好友未读数量
        String friendUnreadCountKey = RedisEnum.FRIEND_UNREAD_COUNT.getKey() + receiverId;

        // 接收人在线 && 打开聊天框是发送人
        if(StringUtils.isNotEmpty(receiverInServer) && senderId.equals(receiverActiveFriend)){
            // 未读消息清零
            redisUtil.hashPut(friendUnreadCountKey, senderId, 0);
        }else{
            /**
             * 如果使用hash先获取，再put +1，时间复杂度是O(1) + O(1)
             * hincrby 是原子操作，时间复杂度是O(1)
             * 时间复杂度：O(1) > O(1) + O(1)
             */

            // 使用redis中的hincrby命令  未读消息 + 1
            redisUtil.hincrby(friendUnreadCountKey, senderId, 1);
        }
    }

    /**
     * 消息未读到消息已读
     * @author wangjinfei
     * @date 2025/8/17 16:33
     * @param messageRequest
     */
    @Override
    public void unread2Read(MessageRequest messageRequest) {
        String senderId = messageRequest.getSenderId();
        String receiverId = messageRequest.getReceiverId();

        // 获取接收人所在的节点信息
        String receiverInServer = centerService.onlineUser(messageRequest.getReceiverId());
        if(StringUtils.isEmpty(receiverInServer)){
            return;
        }

        // 获取接收人打开的聊天窗口好友是谁
        String receiverActiveFriend = redisUtil.get(RedisEnum.CURRENT_CHAT_WINDOW.getKey() + receiverId);
        if (StringUtils.isEmpty(receiverActiveFriend)) {
            return;
        }

        if (!receiverActiveFriend.equals(senderId)) {
            return;
        }

        // 接收人打开的聊天窗口好友是发送人，发送已读通知  （对方在线 && 对方打开的聊天框是发送人）
        // 获取发送人所在的节点信息
        String userInServer = centerService.onlineUser(senderId);
        if (StringUtils.isEmpty(userInServer)) {
            return;
        }
        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                userInServer.split(":")[1] + ":" + userInServer.split(":")[2];
        String value = redisUtil.get(key);
        Client client = JSONObject.parseObject(value, Client.class);

        // 系统推送已读通知
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setSenderId(Sender.SYSTEM.getSender());
        messageResponse.setReceiverId(senderId);
        messageResponse.setMessageType(MessageType.CHAT.getType());
        messageResponse.setSubType(MessageSubType.MES_READ.getType());
        String readChatMessageIds = JSONObject.toJSONString(Arrays.asList(messageRequest.getChatMessageId()));
        String json = JSONObject.of("readChatMessageIds", readChatMessageIds).toJSONString();

        messageResponse.setMessage(json);
        messageResponse.setCreateTime(DateUtil.formatDateTime(LocalDateTime.now(), DateUtil.DATE_TIME_FORMATTER));
        centerService.sendMessage2Client(messageResponse, client);

        // 标记为已读
        messageReadService.markRead(messageRequest.getChatMessageId(), receiverId);
    }
}
