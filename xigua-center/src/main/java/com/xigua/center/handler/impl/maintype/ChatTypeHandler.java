package com.xigua.center.handler.impl.maintype;

import com.xigua.center.handler.base.MessageTypeHandler;
import com.xigua.center.handler.base.SubTypeHandler;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.enums.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ChatSubTypeHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/2 17:42
 */
@Slf4j
@Component
public class ChatTypeHandler implements MessageTypeHandler {
    private final Map<String, SubTypeHandler> subHandlerMap = new HashMap<>();

    // 构造器注入所有的chat子类型处理器
    public ChatTypeHandler(List<SubTypeHandler> allSubHandlers) {
        for (SubTypeHandler subHandler : allSubHandlers) {
            if(MessageType.CHAT.getType().equals(subHandler.getMessageType())){
                subHandlerMap.put(subHandler.getSubType(), subHandler);
            }
        }
    }

    /**
     * 获取主消息类型
     * @author wangjinfei
     * @date 2025/6/2 19:02
     * @return String
     */
    @Override
    public String getMessageType() {
        return MessageType.CHAT.getType();
    }

    /**
     * 根据子类型指定对应的消息处理
     * @author wangjinfei
     * @date 2025/6/2 19:03
     * @param subType
     */
    @Override
    public void dispatchSub(String subType, ChatMessageDTO chatMessageDTO) {
        SubTypeHandler subHandler = subHandlerMap.get(subType);
        log.info("------->>>>>>> 子类型处理器：{}", subHandler);
        if (StringUtils.isNotEmpty(subType)) {
            subHandler.handle(chatMessageDTO);
        } else {
            log.error("未找到子类型处理器: {}", subType);
        }
    }
}
