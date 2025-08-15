package com.xigua.center.message;

import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.enums.MessageSubType;
import com.xigua.domain.enums.MessageType;
import org.springframework.stereotype.Service;

/**
 * @ClassName ChatSendMessageService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/14 22:34
 */
@Service
public class ChatMesSendMessageService extends AbstractMessageService{
    @Override
    public void handleMessage(ChatMessageDTO chatMessageDTO) {

    }

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
}
