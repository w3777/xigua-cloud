package com.xigua.center.wsMessage;

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
public class NotifySwitchChatWindowMessageService extends AbstractMessageService {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String getMessageName() {
        return MessageSubType.SWITCH_CHAT_WINDOW.getDesc();
    }

    @Override
    public String getMessageType() {
        return MessageType.NOTIFY.getType();
    }

    @Override
    public String getMessageSubType() {
        return MessageSubType.SWITCH_CHAT_WINDOW.getType();
    }

    @Override
    public void handleMessage(ChatMessageDTO chatMessageDTO) {
        String userId = chatMessageDTO.getSenderId();
        String receiverId = chatMessageDTO.getReceiverId();

        // 更新当前聊天窗口
        redisUtil.set(RedisEnum.CURRENT_CHAT_WINDOW.getKey() + userId, receiverId);
    }
}
