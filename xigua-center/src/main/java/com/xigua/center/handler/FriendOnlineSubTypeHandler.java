package com.xigua.center.handler;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.connect.Client;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.enums.MessageSubType;
import com.xigua.domain.enums.MessageType;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.service.CenterService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName FriendOnlineSubTypeHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/2 20:19
 */
@Component
public class FriendOnlineSubTypeHandler implements SubTypeHandler{
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CenterService centerService;


    @Override
    public String getSubType() {
        return MessageSubType.FRIEND_ONLINE.getType();
    }

    /**
     * 通知好友用户上线
     * @author wangjinfei
     * @date 2025/6/2 20:38
     * @param chatMessageDTO
     */
    @Override
    public void handle(ChatMessageDTO chatMessageDTO) {
        String userId = chatMessageDTO.getSenderId();

        // 获取和当前用户有好友关系的用户
        Set<String> friendKeys = redisUtil.scanZSetKeysInMember(userId, RedisEnum.FRIEND_RELATION.getKey() + "*");
        if(CollectionUtils.isEmpty(friendKeys)){
            return;
        }

        Set<String> friends = friendKeys.stream()
                .map(key -> key.replace(RedisEnum.FRIEND_RELATION.getKey(), "")) // 去除前缀
                .collect(Collectors.toSet());
        for (String friend : friends) {
            // 判断好友是否在线
            String friendInServer = centerService.onlineUser(friend);
            if(StringUtils.isEmpty(friendInServer)){
                continue;
            }

            // 获取好友所在的节点信息
            String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                    friendInServer.split(":")[1] + ":" + friendInServer.split(":")[2];
            String value = redisUtil.get(key);
            Client client = JSONObject.parseObject(value, Client.class);

            // 通知好友用户上线
            chatMessageDTO.setReceiverId(friend);
            chatMessageDTO.setMessage("");
            chatMessageDTO.setMessageType(MessageType.NOTIFY.getType());
            chatMessageDTO.setSubType(MessageSubType.FRIEND_ONLINE.getType());
            chatMessageDTO.setCreateTime(DateUtil.formatDateTime(LocalDateTime.now(), DateUtil.DATE_TIME_FORMATTER));
            centerService.sendMessage2Client(chatMessageDTO, client);
        }
    }

    @Override
    public String getMessageType() {
        return MessageType.NOTIFY.getType();
    }
}
