package com.xigua.center.handler.impl.subtype.chat;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.center.handler.base.SubTypeHandler;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.connect.Client;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.enums.*;
import com.xigua.service.CenterService;
import com.xigua.service.ChatMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @ClassName MesSendHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/2 17:44
 */
@Component
public class MesSendSubTypeHandler implements SubTypeHandler {
    @Autowired
    private Sequence sequence;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CenterService centerService;
    @Autowired
    private ChatMessageService chatMessageService;

    /**
     * 获取子消息类型
     * @author wangjinfei
     * @date 2025/6/2 19:09
     * @return String
     */
    @Override
    public String getSubType() {
        return MessageSubType.MES_SEND.getType();
    }

    /**
     * 处理消息
     * @author wangjinfei
     * @date 2025/6/2 19:09
     * @param chatMessageDTO
     */
    @Override
    public void handle(ChatMessageDTO chatMessageDTO) {
        String chatMessageId = sequence.nextNo();
        String receiverId = chatMessageDTO.getReceiverId();
        chatMessageDTO.setChatMessageId(chatMessageId);

        // ack处理 (回传消息id给发送人)
        ackHandle(chatMessageDTO);

        // todo 下面所有逻辑处理都可以放到mq里做排队异步处理，提高系统吞吐量

        // db处理 mysql持久化
        messageDbHandle(chatMessageDTO);

        // redis处理 (最后消息好友列表、好友最后消息)
        redisHandle(chatMessageDTO);

        // 获取接收人所在的节点信息
        String receiverInServer = centerService.onlineUser(receiverId);

        // 接收人在线，处理消息
        receiverHande(chatMessageDTO, receiverInServer);

        // 好友未读消息处理
        friendUnreadHandle(chatMessageDTO, receiverInServer);

        // 判断接收者是否在线
        if(StringUtils.isEmpty(receiverInServer)){
            // 如果接收人不在线，直接返回，不做后续处理
            return;
        }

        // 发送人在线，处理消息
        senderHande(chatMessageDTO);
    }

    /**
     * 获取主消息类型
     * @author wangjinfei
     * @date 2025/6/2 20:03
     * @return String
     */
    @Override
    public String getMessageType() {
        return MessageType.CHAT.getType();
    }

    /**
     * ack处理
     * 回传消息id给发送人
     * id在服务端生成，客户端应该第一时间拿到消息id，也为后续已读做铺垫
     * @author wangjinfei
     * @date 2025/6/6 21:02
     * @param chatMessageDTO
    */
    private void ackHandle(ChatMessageDTO chatMessageDTO){
        String senderId = chatMessageDTO.getSenderId();
        // 获取发送人所在的节点信息
        String userInServer = centerService.onlineUser(senderId);
        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                userInServer.split(":")[1] + ":" + userInServer.split(":")[2];

        String value = redisUtil.get(key);
        Client client = JSONObject.parseObject(value, Client.class);

        // 发送当前消息id到客户端 （id在服务端生成，客户端应该第一时间拿到消息id，也为后续已读做铺垫）
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSenderId(Sender.SYSTEM.getSender());
        dto.setReceiverId(senderId);
        dto.setMessageType(MessageType.CHAT.getType());
        dto.setSubType(MessageSubType.MES_SEND_ACK.getType());
        // kv (k消息内容, v消息id)
        String json = JSONObject.of(chatMessageDTO.getMessage(), chatMessageDTO.getChatMessageId()).toJSONString();
        dto.setMessage(json);
        // 客户端传过来时间戳，服务端不做处理直接回传
        // 消息内容重复也能根据客户端时间戳 + 消息内容，找到消息并把消息id赋值
        dto.setCreateTime(chatMessageDTO.getCreateTime());
        centerService.sendMessage2Client(dto, client);
    }

    /**
     * 接收人在线，处理消息
     * @author wangjinfei
     * @date 2025/6/3 20:55
     * @param chatMessageDTO
     * @param receiverInServer
    */
    private void receiverHande(ChatMessageDTO chatMessageDTO, String receiverInServer){
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
        chatMessageDTO.setSubType(MessageSubType.MES_RECEIVE.getType());
        centerService.sendMessage2Client(chatMessageDTO, client);
    }

    /**
     * 发送人在线，处理消息
     * @author wangjinfei
     * @date 2025/6/3 20:56
     * @param chatMessageDTO
    */
    private void senderHande(ChatMessageDTO chatMessageDTO){
        String senderId = chatMessageDTO.getSenderId();
        String receiverId = chatMessageDTO.getReceiverId();

        // 获取接收人打开的聊天窗口好友是谁
        String receiverActiveFriend = redisUtil.get(RedisEnum.CURRENT_ACTIVE_FRIEND.getKey() + receiverId);
        if(StringUtils.isEmpty(receiverActiveFriend)){
            return;
        }

        if(!receiverActiveFriend.equals(senderId)){
            return;
        }

        // 接收人打开的聊天窗口好友是发送人，发送已读通知  （对方在线 && 对方打开的聊天框是发送人）
        // 获取发送人所在的节点信息
        String userInServer = centerService.onlineUser(senderId);
        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                userInServer.split(":")[1] + ":" + userInServer.split(":")[2];
        String value = redisUtil.get(key);
        Client client = JSONObject.parseObject(value, Client.class);

        // 系统推送已读通知
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSenderId(Sender.SYSTEM.getSender());
        dto.setReceiverId(senderId);
        dto.setMessageType(MessageType.CHAT.getType());
        dto.setSubType(MessageSubType.MES_READ.getType());
        String readChatMessageIds = JSONObject.toJSONString(Arrays.asList(chatMessageDTO.getChatMessageId()));
        String json = JSONObject.of("readChatMessageIds", readChatMessageIds).toJSONString();

        dto.setMessage(json);
        dto.setCreateTime(DateUtil.formatDateTime(LocalDateTime.now(), DateUtil.DATE_TIME_FORMATTER));
        centerService.sendMessage2Client(dto, client);

        // 修改消息状态为已读  todo 可以优化成异步处理，减少阻塞
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(chatMessageDTO.getChatMessageId());
        chatMessage.setIsRead(ChatMessageIsRead.READ.getType());
        chatMessage.setReadTime(LocalDateTime.now());
        chatMessage.setUpdateBy(receiverId);
        chatMessage.setUpdateTime(LocalDateTime.now());
        chatMessageService.updateById(chatMessage);
    }

    /**
     * db处理 mysql持久化
     * @author wangjinfei
     * @date 2025/5/20 22:02
     * @param chatMessageDTO
     */
    private void messageDbHandle(ChatMessageDTO chatMessageDTO){
        String senderId = chatMessageDTO.getSenderId();
        String receiverId = chatMessageDTO.getReceiverId();
        String message = chatMessageDTO.getMessage();

        // 封装聊天消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(chatMessageDTO.getChatMessageId());
        chatMessage.setSenderId(senderId);
        chatMessage.setReceiverId(receiverId);
        chatMessage.setMessage(message);
        // ws拿不到threadLocal存储的当前用户 （此时的发送人就是创建人）
        chatMessage.setCreateBy(senderId);
        // 消息默认全部未读，已读状态由前端主动修改
        chatMessage.setIsRead(ChatMessageIsRead.UNREAD.getType());
        chatMessageService.save(chatMessage);
        // 注意 不采用双写同步es，后期用同步工具解决，减少侵入性
    }


    /**
     * redis处理
     * 最后消息好友列表 zset
     * 好友最后消息 hash
     * @author wangjinfei
     * @date 2025/6/9 20:04
     * @param chatMessageDTO
    */
    private void redisHandle(ChatMessageDTO chatMessageDTO){
        String senderId = chatMessageDTO.getSenderId();
        String receiverId = chatMessageDTO.getReceiverId();
        long timestamp = System.currentTimeMillis();

        // 存储redis 最后消息的好友（这样的key可以保证唯一）
        redisUtil.zsadd(RedisEnum.LAST_MES_FRIEND.getKey() + receiverId, senderId, timestamp);
        // 存储redis 最后消息
        redisUtil.hashPut(RedisEnum.LAST_MES.getKey() + receiverId, senderId, JSONObject.toJSONString(chatMessageDTO));
    }

    /**
     * 好友未读消息处理
     * @author wangjinfei
     * @date 2025/6/9 20:44
     * @param chatMessageDTO
     * @param receiverInServer
    */
    private void friendUnreadHandle(ChatMessageDTO chatMessageDTO, String receiverInServer){
        String senderId = chatMessageDTO.getSenderId();
        String receiverId = chatMessageDTO.getReceiverId();

        // 获取接收人打开的聊天窗口好友是谁
        String receiverActiveFriend = redisUtil.get(RedisEnum.CURRENT_ACTIVE_FRIEND.getKey() + receiverId);

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


//        // 对方在线 && 打开聊天框不是发送人
//        // 实时推送好友未读消息数量（小红点）
//        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
//                receiverInServer.split(":")[1] + ":" + receiverInServer.split(":")[2];
//        String value = redisUtil.get(key);
//        Client client = JSONObject.parseObject(value, Client.class);

        // 系统推送小红点
//        ChatMessageDTO dto = new ChatMessageDTO();
//        dto.setSenderId(senderId);
//        dto.setReceiverId(receiverId);
//        dto.setMessageType(MessageType.UNREAD.getType());
//        dto.setSubType(MessageSubType.FRIEND_UNREAD.getType());
//        dto.setMessage(friendUnreadCount.toString());
//        dto.setCreateTime(DateUtil.formatDateTime(LocalDateTime.now(), DateUtil.DATE_TIME_FORMATTER));
//        centerService.sendMessage2Client(dto, client);
    }
}
