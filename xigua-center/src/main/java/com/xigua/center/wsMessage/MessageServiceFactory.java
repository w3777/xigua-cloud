package com.xigua.center.wsMessage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MessageHandlerFactory
 * @Description
 * @Author wangjinfei
 * @Date 2025/6/2 17:55
 * websocket消息工厂，获取消息服务
 */
@Slf4j
@Component
public class MessageServiceFactory {
    private static final Map<String, Map<String, AbstractMessageService>> serviceMap = new HashMap<>();

    // 构造函数注入所有的主类型处理器
    public MessageServiceFactory(List<AbstractMessageService> messageServices) {
        if(CollectionUtils.isEmpty(messageServices)){
            log.info("--------->>>>>  消息实现服务为空");
            return;
        }

        for (AbstractMessageService messageService : messageServices) {
            String messageType = messageService.getMessageType();
            if(StringUtils.isEmpty(messageType)){
                log.info("--------->>>>>  消息主类型为空");
                continue;
            }
            String messageSubType = messageService.getMessageSubType();
            if(StringUtils.isEmpty(messageSubType)){
                log.info("--------->>>>>  消息主类型为空");
                continue;
            }

            Map<String, AbstractMessageService> subMap = serviceMap.computeIfAbsent(messageType, k -> new HashMap<>());
            subMap.put(messageSubType, messageService);
            log.info("--------->>>>>  {} - 消息服务注册成功", messageService.getMessageName());
        }
    }

    /**
     * 根据主类型和子类型获取消息service
     * @author wangjinfei
     * @date 2025/8/14 22:00
     * @param messageType
     * @param messageSubType
     * @return AbstractMessageService
    */
    public static AbstractMessageService getMessageService(String messageType, String messageSubType) {
        log.info("------->>>>>>> 消息主类型：{}，消息子类型：{}", messageType, messageSubType);
        if(messageType == null){
            log.info("--------->>>>>  消息主类型为空");
            return null;
        }
        if(messageSubType == null){
            log.info("--------->>>>>  消息子类型为空");
            return null;
        }

        Map<String, AbstractMessageService> mainMap = serviceMap.get(messageType);
        if (mainMap == null) {
            log.info("--------->>>>>  未找到主map，消息主类型：{}", messageType);
            return null;
        }

        AbstractMessageService abstractMessageService = mainMap.get(messageSubType);
        if(abstractMessageService == null){
            log.info("--------->>>>>  未找到子service，消息子类型：{}", messageSubType);
            return null;
        }
        return abstractMessageService;
    }
}
