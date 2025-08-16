package com.xigua.center.message;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.api.service.MessageReadService;
import com.xigua.center.message.AbstractMessageService;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.bo.LastMessageBO;
import com.xigua.domain.bo.LastMessageContentBO;
import com.xigua.domain.connect.Client;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.entity.MessageRead;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.*;
import com.xigua.api.service.CenterService;
import com.xigua.api.service.ChatMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @ClassName MesSendHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/2 17:44
 */
@Component
public class ChatMesSendMessageService extends AbstractMessageService {
    @Autowired
    private Sequence sequence;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CenterService centerService;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private MessageReadService messageReadService;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public String getMessageName() {
        return MessageSubType.MES_SEND.getDesc();
    }

    @Override
    public String getMessageType() {
        return MessageType.CHAT.getType();
    }

    @Override
    public String getMessageSubType() {
        return MessageSubType.MES_SEND.getType();
    }

    @Override
    public void handleMessage(ChatMessageDTO chatMessageDTO) {
        String chatMessageId = sequence.nextNo();
        String receiverId = chatMessageDTO.getReceiverId();
        chatMessageDTO.setChatMessageId(chatMessageId);

        // 定义事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        // 开启事务
        TransactionStatus status = transactionManager.getTransaction(def);

        // db处理 mysql持久化
        messageDbHandle(chatMessageDTO);

        // ack处理 (回传消息id给发送人、超时回滚)
        ackHandle(chatMessageDTO, status);

        // 执行到这里没有异常 提交事务（保证mysql入库）
        transactionManager.commit(status);

        // todo 下面所有逻辑处理都可以放到mq里做排队异步处理，提高系统吞吐量

        // redis处理 (最后消息好友列表、好友最后消息)
        redisHandle(chatMessageDTO);

        // 获取接收人所在的节点信息
        String receiverInServer = centerService.onlineUser(receiverId);

        // 接收人在线，处理消息
        receiverHande(chatMessageDTO, receiverInServer);

        // 好友未读消息处理
        friendUnreadHandle(chatMessageDTO, receiverInServer);

        // 接收人在线
        if(StringUtils.isNotEmpty(receiverInServer)){
            // 给发送人发送消息已读
            senderHande(chatMessageDTO);
        }
    }

    /**
     * ack处理
     * 回传消息id给发送人
     * id在服务端生成，客户端应该第一时间拿到消息id，也为后续已读做铺垫
     * 超时回滚
     * @author wangjinfei
     * @date 2025/6/6 21:02
     * @param chatMessageDTO
    */
    private void ackHandle(ChatMessageDTO chatMessageDTO, TransactionStatus status){
        String senderId = chatMessageDTO.getSenderId();
        // 获取发送人所在的节点信息
        String userInServer = centerService.onlineUser(senderId);
        if(StringUtils.isEmpty(userInServer)){
            return;
        }
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

        // 发送ack之前，判断是否需要发送ack
        sendAckBefore(chatMessageDTO, status);

        centerService.sendMessage2Client(dto, client);
    }

    /**
     * 发送ack之前，判断是否需要发送ack
     * @author wangjinfei
     * @date 2025/8/12 21:29
    */
    private void sendAckBefore(ChatMessageDTO chatMessageDTO, TransactionStatus status){
        if(chatMessageDTO.getServerReceiveTime() == null){
            transactionManager.rollback(status);
            throw new BusinessException("--------->>>>>>> 服务端收到消息的时间不能为空");
        }

        // 模拟超时，可以验证客户端1500毫秒未收到ack导致的消息发送失败
//        mockTimeout(status);

        // 前端发送消息后1500毫秒没有接收到服务端的ack，认为发送失败
        long sendAckBeforeTime = System.currentTimeMillis();
        if(sendAckBeforeTime - chatMessageDTO.getServerReceiveTime() > 1400){ // 服务端要判断1400毫秒
            transactionManager.rollback(status);
            // 系统报错，不往下进行任何处理
            throw new BusinessException("--------->>>>>>> 服务端收到消息的时间与当前时间差不能超过1400毫秒");
        }
    }

    /**
     * 模拟超时
     * @author wangjinfei
     * @date 2025/8/13 8:10
     * @param status
    */
    private void mockTimeout(TransactionStatus status){
        try{
            // 假标志 控制是否阻塞
            String flag = redisUtil.get("flag");
            if(StringUtils.isEmpty(flag)){
                Thread.sleep(1800);
            }
        }catch (Exception e){
            transactionManager.rollback(status);
            e.printStackTrace();
        }
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
        String receiverActiveFriend = redisUtil.get(RedisEnum.CURRENT_CHAT_WINDOW.getKey() + receiverId);
        if(StringUtils.isEmpty(receiverActiveFriend)){
            return;
        }

        if(!receiverActiveFriend.equals(senderId)){
            return;
        }

        // 接收人打开的聊天窗口好友是发送人，发送已读通知  （对方在线 && 对方打开的聊天框是发送人）
        // 获取发送人所在的节点信息
        String userInServer = centerService.onlineUser(senderId);
        if(StringUtils.isEmpty(userInServer)){
            return;
        }
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
        messageReadService.markRead(chatMessageDTO.getChatMessageId(), receiverId);
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
        Integer chatType = chatMessageDTO.getChatType();

        // 封装聊天消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(chatMessageDTO.getChatMessageId());
        chatMessage.setSenderId(senderId);
        chatMessage.setReceiverId(receiverId);
        chatMessage.setMessage(message);
        chatMessage.setChatType(chatType);
        // ws拿不到threadLocal存储的当前用户 （此时的发送人就是创建人）
        chatMessage.setCreateBy(senderId);
        // 添加聊天消息
        chatMessageService.save(chatMessage);

        // 添加消息未读
        MessageRead messageRead = new MessageRead();
        messageRead.setId(sequence.nextNo());
        messageRead.setMessageId(chatMessageDTO.getChatMessageId());
        messageRead.setSenderId(senderId);
        messageRead.setReceiverId(receiverId);
        messageRead.setIsRead(MessageReadStatus.UNREAD.getType());
        // ws拿不到threadLocal存储的当前用户 （此时的发送人就是创建人）
        messageRead.setCreateBy(senderId);
        messageReadService.save(messageRead);

        /**
         * todo 注意同步es
         * 1.不采用双写同步es，后期用同步工具解决，减少侵入性  (企业级项目可以采用这种方式，但需要额外搭建实时流)
         * 2.用kafka异步写入es，减少阻塞  (简单，但有代码侵入性)  后续可能会采用这种方式
        */
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

        // 存储redis 最后消息
        redisUtil.zsadd(RedisEnum.LAST_MES.getKey() + receiverId, senderId, timestamp);
        // 存储redis 最后消息内容
        LastMessageBO lastMessageBO = chatMessageDTO2LastMessageBO(chatMessageDTO);
        redisUtil.hashPut(RedisEnum.LAST_MES_CONTENT.getKey() + receiverId, senderId, JSONObject.toJSONString(lastMessageBO));
    }

    /**
     * 封装最后消息内容
     * @author wangjinfei
     * @date 2025/7/18 13:46
     * @param chatMessageDTO
     * @return LastMessageBO
    */
    private LastMessageBO chatMessageDTO2LastMessageBO(ChatMessageDTO chatMessageDTO){
        LastMessageBO lastMessageBO = new LastMessageBO();
        LastMessageContentBO lastMessageContent = new LastMessageContentBO();
        String senderId = chatMessageDTO.getSenderId();

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
        lastMessageContent.setContent(chatMessageDTO.getMessage());
        lastMessageBO.setLastMessageContent(lastMessageContent);

        return lastMessageBO;
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
