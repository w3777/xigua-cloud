package com.xigua.center.handler.impl.subtype.notify;

import com.xigua.center.handler.base.SubTypeHandler;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.enums.MessageSubType;
import com.xigua.domain.enums.MessageType;
import com.xigua.domain.enums.RedisEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName SwitchFriendSubTypeHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/3 20:34
 */
@Component
public class SwitchFriendSubTypeHandler implements SubTypeHandler {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String getSubType() {
        return MessageSubType.SWITCH_FRIEND.getType();
    }

    @Override
    public void handle(ChatMessageDTO chatMessageDTO) {
        String userId = chatMessageDTO.getSenderId();
        String receiverId = chatMessageDTO.getReceiverId();

        // 更新当前激活的好友
        redisUtil.set(RedisEnum.CURRENT_ACTIVE_FRIEND.getKey() + userId, receiverId);
    }

    @Override
    public String getMessageType() {
        return MessageType.NOTIFY.getType();
    }
}
