package com.xigua.center.handler;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.connect.Client;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.enums.ChatMessageIsRead;
import com.xigua.domain.enums.MessageSubType;
import com.xigua.domain.enums.MessageType;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.service.CenterService;
import com.xigua.service.ChatMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName MesSendHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/2 17:44
 */
@Component
public class MesSendSubTypeHandler implements SubTypeHandler{
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
        String receiverId = chatMessageDTO.getReceiverId();
        // 判断接收者是否在线
        String userInServer = centerService.onlineUser(receiverId);
        if(StringUtils.isEmpty(userInServer)){
            // 这里做离线消息存储
            // db处理 mysql持久化 redis缓存
            messageDbHandle(chatMessageDTO);
            return;
        }

        // 获取接收者所在的节点信息
        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                userInServer.split(":")[1] + ":" + userInServer.split(":")[2];
        String value = redisUtil.get(key);
        Client client = JSONObject.parseObject(value, Client.class);

        // 实时推送消息，发送到接收人所在节点
        chatMessageDTO.setSubType(MessageSubType.MES_RECEIVE.getType());
        centerService.sendMessage2Client(chatMessageDTO, client);

        // db处理 mysql持久化 redis缓存
        messageDbHandle(chatMessageDTO);
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
     * db处理 mysql持久化 redis缓存最后聊天消息
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
        chatMessage.setId(sequence.nextNo());
        chatMessage.setSenderId(senderId);
        chatMessage.setReceiverId(receiverId);
        chatMessage.setMessage(message);
        // ws拿不到threadLocal存储的当前用户 （此时的发送人就是创建人）
        chatMessage.setCreateBy(senderId);
        // 消息默认全部未读，已读状态由前端主动修改
        chatMessage.setIsRead(ChatMessageIsRead.UNREAD.getType());
        chatMessageService.save(chatMessage);
        // 注意 不采用双写同步es，后期用同步工具解决，减少侵入性

        long timestamp = System.currentTimeMillis();
        // 存储redis 最后消息的好友（这样的key可以保证唯一）
        redisUtil.zsadd(RedisEnum.LAST_MES_FRIEND.getKey() + receiverId, senderId, timestamp);
        // 存储redis 最后消息
        redisUtil.hashPut(RedisEnum.LAST_MES.getKey() + receiverId, senderId, JSONObject.toJSONString(chatMessageDTO));
    }
}
