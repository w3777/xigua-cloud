package com.xigua.center.handler.impl.subtype.heartbeat;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.center.handler.base.SubTypeHandler;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.connect.Client;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.enums.MessageSubType;
import com.xigua.domain.enums.MessageType;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.domain.enums.Sender;
import com.xigua.service.CenterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @ClassName SwitchFriendSubTypeHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/3 20:34
 */
@Component
public class PingSubTypeHandler implements SubTypeHandler {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CenterService centerService;

    @Override
    public String getSubType() {
        return MessageSubType.PING.getType();
    }

    @Override
    public void handle(ChatMessageDTO chatMessageDTO) {
        String userId = chatMessageDTO.getSenderId();

        // 更新最后心跳时间
        redisUtil.set(RedisEnum.LAST_PING_TIME.getKey() + userId, String.valueOf(System.currentTimeMillis()));

        // 发送心跳回复
        resPong(userId);

        /** 
         * todo 客户端发送的ping，以及服务端响应的pong，是可以做持久化的
        */
    }

    @Override
    public String getMessageType() {
        return MessageType.HEART_BEAT.getType();
    }

    /**
     * 发送心跳回复
     * @author wangjinfei
     * @date 2025/6/16 20:33
     * @param senderId
    */
    private void resPong(String senderId){
        // 获取接收者所在节点
        String userInServer = centerService.onlineUser(senderId);
        if(StringUtils.isEmpty(userInServer)){
            return;
        }

        // 获取接收者所在的节点信息
        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                userInServer.split(":")[1] + ":" + userInServer.split(":")[2];
        String value = redisUtil.get(key);
        Client client = JSONObject.parseObject(value, Client.class);

        // 发送心跳回复
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderId(Sender.SYSTEM.getSender());
        chatMessageDTO.setReceiverId(senderId);
        chatMessageDTO.setMessageType(MessageType.HEART_BEAT.getType());
        chatMessageDTO.setSubType(MessageSubType.PONG.getType());
        chatMessageDTO.setMessage("pong");
        chatMessageDTO.setCreateTime(DateUtil.formatDateTime(LocalDateTime.now(), DateUtil.DATE_TIME_FORMATTER));
        centerService.sendMessage2Client(chatMessageDTO, client);
    }
}
