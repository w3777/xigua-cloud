package com.xigua.center.chatMessage;

import com.xigua.domain.enums.ChatType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ChatMessageServiceFactory
 * @Description 发送聊天消息服务工厂
 * @Author wangjinfei
 * @Date 2025/8/17 16:11
 */
@Slf4j
@Component
public class SendChatMessageServiceFactory {
    private static final Map<String, AbstractSendChatMessageService> serviceMap = new HashMap<>();

    public SendChatMessageServiceFactory(List<AbstractSendChatMessageService> chatMessageServiceList) {
        if(CollectionUtils.isEmpty(chatMessageServiceList)){
            log.info("--------->>>>>  发送聊天消息实现服务为空");
            return;
        }

        for (AbstractSendChatMessageService chatMessageService : chatMessageServiceList) {
            ChatType chatTypeE = chatMessageService.getChatType();
            serviceMap.put(chatTypeE.getName(), chatMessageService);
            log.info("--------->>>>>  {} - 发送聊天消息服务注册成功", chatTypeE.getDesc());
        }
    }


    /**
     * 获取聊天消息服务
     * @author wangjinfei
     * @date 2025/8/17 16:21
     * @param chatType
     * @return AbstractChatMessageService
    */
    public static AbstractSendChatMessageService getSendService(String chatType) {
        if(StringUtils.isEmpty(chatType)){
            log.info("--------->>>>>  发送聊天消息类型为空");
            return null;
        }

        AbstractSendChatMessageService abstractSendChatMessageService = serviceMap.get(chatType);
        if(abstractSendChatMessageService == null) {
            log.info("--------->>>>>  {} - 发送聊天消息服务不存在", chatType);
            return null;
        }

        return abstractSendChatMessageService;
    }
}
