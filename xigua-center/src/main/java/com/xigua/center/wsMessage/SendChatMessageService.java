package com.xigua.center.wsMessage;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.center.chatMessage.AbstractSendChatMessageService;
import com.xigua.center.chatMessage.SendChatMessageServiceFactory;
import com.xigua.center.validator.ChatValidateCode;
import com.xigua.center.validator.MessagePermissionValidator;
import com.xigua.center.validator.ValidatorFactory;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.connect.Client;
import com.xigua.domain.ws.MessageRequest;
import com.xigua.domain.enums.*;
import com.xigua.api.service.CenterService;
import com.xigua.domain.ws.MessageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @ClassName MesSendHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/2 17:44
 */
@Component
public class SendChatMessageService extends AbstractMessageService {
    @Autowired
    private Sequence sequence;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CenterService centerService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private ValidatorFactory validatorFactory;

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
    public void handleMessage(MessageRequest messageRequest) {
        String chatMessageId = sequence.nextNo();
        String receiverId = messageRequest.getReceiverId();
        messageRequest.setChatMessageId(chatMessageId);

        // 定义事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        // 开启事务
        TransactionStatus status = transactionManager.getTransaction(def);

        // db处理 mysql持久化
        messageDbHandle(messageRequest);

        // ack处理 (回传消息id给发送人、超时回滚)
        ackHandle(messageRequest, status);

        // 执行到这里没有异常 提交事务（保证mysql入库）
        transactionManager.commit(status);

        // todo 下面所有逻辑处理都可以放到mq里做排队异步处理，提高系统吞吐量

        // 校验消息权限
        ChatValidateCode validateCode = validateMessage(messageRequest);
        if(!validateCode.equals(ChatValidateCode.SUCCESS)){
            // 根据校验code 处理失败
            validateFail(validateCode, messageRequest);
            return;
        }

        // 最后一条消息  (消息列表)
        lastMessage(messageRequest);

        // 聊天消息发送到接收人
        chatMessage2Receiver(messageRequest);

        // 未读消息数量
        unreadMessageCount(messageRequest);

        // 消息未读到消息已读
        unread2Read(messageRequest);
    }

    /**
     * ack处理
     * 回传消息id给发送人
     * id在服务端生成，客户端应该第一时间拿到消息id，也为后续已读做铺垫
     * 超时回滚
     * @author wangjinfei
     * @date 2025/6/6 21:02
     * @param messageRequest
    */
    private void ackHandle(MessageRequest messageRequest, TransactionStatus status){
        String senderId = messageRequest.getSenderId();
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
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setSenderId(Sender.SYSTEM.getSender());
        messageResponse.setReceiverId(senderId);
        messageResponse.setMessageType(MessageType.CHAT.getType());
        messageResponse.setSubType(MessageSubType.MES_SEND_ACK.getType());
        // kv (k消息内容, v消息id)
        String json = JSONObject.of(messageRequest.getMessage(), messageRequest.getChatMessageId()).toJSONString();
        messageResponse.setMessage(json);
        // 客户端传过来时间戳，服务端不做处理直接回传
        // 消息内容重复也能根据客户端时间戳 + 消息内容，找到消息并把消息id赋值
        messageResponse.setCreateTime(messageRequest.getCreateTime());

        // 发送ack之前，判断是否需要发送ack
        sendAckBefore(messageRequest, status);

        centerService.sendMessage2Client(messageResponse, client);
    }

    /**
     * 发送ack之前，判断是否需要发送ack
     * @author wangjinfei
     * @date 2025/8/12 21:29
    */
    private void sendAckBefore(MessageRequest messageRequest, TransactionStatus status){
        if(messageRequest.getServerReceiveTime() == null){
            transactionManager.rollback(status);
            throw new BusinessException("--------->>>>>>> 服务端收到消息的时间不能为空");
        }

        // 模拟超时，可以验证客户端1500毫秒未收到ack导致的消息发送失败
//        mockTimeout(status);

        // 前端发送消息后1500毫秒没有接收到服务端的ack，认为发送失败
        long sendAckBeforeTime = System.currentTimeMillis();
        if(sendAckBeforeTime - messageRequest.getServerReceiveTime() > 1400){ // 服务端要判断1400毫秒
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
     * 校验消息权限
     * @author wangjinfei
     * @date 2025/9/13 18:12
     * @param messageRequest
     * @return Boolean
    */
    private ChatValidateCode validateMessage(MessageRequest messageRequest){
        Integer chatType = messageRequest.getChatType();
        if(chatType == null){
            throw new BusinessException("--------->>>>>>> 聊天类型不能为空");
        }

        ChatType chatTypeE = ChatType.getChatType(chatType);
        if(chatTypeE == null){
            throw new BusinessException("--------->>>>>>> 聊天类型不存在");
        }

        // 获取校验器
        MessagePermissionValidator validator = validatorFactory.getValidator(chatTypeE);
        if(validator == null){
            throw new BusinessException("--------->>>>>>> 校验器不存在");
        }

        // 校验权限
        ChatValidateCode chatValidateCode = validator.validatePermission(messageRequest);

        return chatValidateCode;
    }

    /**
     * 校验失败处理
     * @author wangjinfei
     * @date 2025/9/13 18:23
     * @param chatValidateCode
    */
    private void validateFail(ChatValidateCode chatValidateCode, MessageRequest messageRequest){
        // todo 这个方法的处理写死了，应该抽出一层

        if(chatValidateCode == null){
            return;
        }
        // 获取发送人所在的节点信息
        String senderInServer = centerService.onlineUser(messageRequest.getSenderId());
        if(StringUtils.isEmpty(senderInServer)){
            // 发送人不在线，直接返回，不做后续处理
            return;
        }

        // 获取发送人所在的节点信息
        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                senderInServer.split(":")[1] + ":" + senderInServer.split(":")[2];
        String value = redisUtil.get(key);
        Client client = JSONObject.parseObject(value, Client.class);

        // 实时推送消息，发送到发送人所在节点
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setSenderId(Sender.SYSTEM.getSender());
        messageResponse.setReceiverId(messageRequest.getSenderId());
        messageResponse.setMessageType(MessageType.CHAT.getType());
        messageResponse.setSubType(MessageSubType.SYSTEM_MES.getType());
        messageResponse.setMessage(chatValidateCode.getMsg());
        messageResponse.setCreateTime(String.valueOf(System.currentTimeMillis()));
        centerService.sendMessage2Client(messageResponse, client);
    }

    /**
     * 聊天消息发送到接收人
     * @author wangjinfei
     * @date 2025/6/3 20:55
     * @param messageRequest
    */
    private void chatMessage2Receiver(MessageRequest messageRequest){
        ChatType chatTypeE = ChatType.getChatType(messageRequest.getChatType());
        // 调用不同的实现类
        AbstractSendChatMessageService chatMessageService = SendChatMessageServiceFactory.getSendService(chatTypeE.getName());
        chatMessageService.chatMessage2Receiver(messageRequest);
    }

    /**
     * 消息未读到消息已读
     * @author wangjinfei
     * @date 2025/6/3 20:56
     * @param messageRequest
    */
    private void unread2Read(MessageRequest messageRequest){
        ChatType chatTypeE = ChatType.getChatType(messageRequest.getChatType());
        // 调用不同的实现类
        AbstractSendChatMessageService chatMessageService = SendChatMessageServiceFactory.getSendService(chatTypeE.getName());
        chatMessageService.unread2Read(messageRequest);
    }

    /**
     * db处理 mysql持久化
     * @author wangjinfei
     * @date 2025/5/20 22:02
     * @param messageRequest
     */
    private void messageDbHandle(MessageRequest messageRequest){
        Integer chatType = messageRequest.getChatType();
        ChatType chatTypeE = ChatType.getChatType(chatType);

        // 根据不同聊天类型，调用不同的实现类
        AbstractSendChatMessageService chatMessageService = SendChatMessageServiceFactory.getSendService(chatTypeE.getName());

        // 保存聊天消息
        chatMessageService.saveChatMessage(messageRequest);

        // 保存消息未读
        chatMessageService.saveMessageUnread(messageRequest);

        /**
         * todo 注意同步es
         * 1.不采用双写同步es，后期用同步工具解决，减少侵入性  (企业级项目可以采用这种方式，但需要额外搭建实时流)
         * 2.用kafka异步写入es，减少阻塞  (简单，但有代码侵入性)  后续可能会采用这种方式
        */
    }


    /**
     * 最后一条消息  (消息列表)
     * 最后消息好友列表 zset
     * 好友最后消息 hash
     * @author wangjinfei
     * @date 2025/6/9 20:04
     * @param messageRequest
    */
    private void lastMessage(MessageRequest messageRequest){
        ChatType chatTypeE = ChatType.getChatType(messageRequest.getChatType());
        // 调用不同的实现类
        AbstractSendChatMessageService chatMessageService = SendChatMessageServiceFactory.getSendService(chatTypeE.getName());
        chatMessageService.lastMessage(messageRequest);
    }

    /**
     * 未读消息数量
     * @author wangjinfei
     * @date 2025/6/9 20:44
     * @param messageRequest
    */
    private void unreadMessageCount(MessageRequest messageRequest){
        ChatType chatTypeE = ChatType.getChatType(messageRequest.getChatType());
        // 调用不同的实现类
        AbstractSendChatMessageService chatMessageService = SendChatMessageServiceFactory.getSendService(chatTypeE.getName());
        chatMessageService.unreadMessageCount(messageRequest);
    }
}
