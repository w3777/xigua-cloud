package com.xigua.center.factory;

import com.xigua.center.handler.base.MessageTypeHandler;
import com.xigua.domain.dto.ChatMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName MessageHandlerFactory
 * @Description
 * @Author wangjinfei
 * @Date 2025/6/2 17:55
 * 工厂类，用于根据主类型和子类型找到对应的消息处理器
 */
@Slf4j
@Component
public class MessageHandlerFactory {
    private final Map<String, MessageTypeHandler> mainHandlerMap = new HashMap<>();

    // 构造函数注入所有的主类型处理器
    public MessageHandlerFactory(List<MessageTypeHandler> mainHandlers) {
        for (MessageTypeHandler handler : mainHandlers) {
            mainHandlerMap.put(handler.getMessageType(), handler);
        }
    }

    /**
     * 根据主类型和子类型找到对应的消息处理器并处理消息
     * @author wangjinfei
     * @date 2025/6/2 19:56
     * @param mainType
     * @param subType
     * @param chatMessageDTO
    */
    public void dispatch(String mainType, String subType, ChatMessageDTO chatMessageDTO) {
        MessageTypeHandler maninHandler = mainHandlerMap.get(mainType);
        log.info("------->>>>>>> 主类型处理器：{}", maninHandler);
        if (maninHandler != null) {
            maninHandler.dispatchSub(subType, chatMessageDTO);
        } else {;
            log.error("未找到主类型处理器: {}", mainType);
        }
    }
}
