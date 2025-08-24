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
 * @ClassName ReadChatMessageServiceFactory
 * @Description 已读聊天消息服务工厂
 * @Author wangjinfei
 * @Date 2025/8/24 11:36
 */
@Slf4j
@Component
public class ReadChatMessageServiceFactory {
    private static final Map<String, AbstractReadChatMessageService> serviceMap = new HashMap<>();

    public ReadChatMessageServiceFactory(List<AbstractReadChatMessageService> readChatMessageServiceList) {
        if(CollectionUtils.isEmpty(readChatMessageServiceList)){
            log.info("--------->>>>>  已读聊天消息实现服务为空");
            return;
        }

        for (AbstractReadChatMessageService readChatMessageService : readChatMessageServiceList) {
            ChatType chatTypeE = readChatMessageService.getChatType();
            serviceMap.put(chatTypeE.getName(), readChatMessageService);
            log.info("--------->>>>>  {} - 已读聊天消息服务注册成功", chatTypeE.getDesc());
        }
    }


    /**
     * 获取聊天消息服务
     * @author wangjinfei
     * @date 2025/8/17 16:21
     * @param chatType
     * @return AbstractChatMessageService
     */
    public static AbstractReadChatMessageService getReadService(String chatType) {
        if(StringUtils.isEmpty(chatType)){
            log.info("--------->>>>>  已读聊天消息类型为空");
            return null;
        }

        AbstractReadChatMessageService AbstractReadChatMessageService = serviceMap.get(chatType);
        if(AbstractReadChatMessageService == null) {
            log.info("--------->>>>>  {} - 已读聊天消息服务不存在", chatType);
            return null;
        }

        return AbstractReadChatMessageService;
    }
}
