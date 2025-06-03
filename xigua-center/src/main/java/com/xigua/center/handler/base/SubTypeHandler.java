package com.xigua.center.handler.base;

import com.xigua.domain.dto.ChatMessageDTO;

/**
 * @ClassName SubTypeHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/2 17:42
 */
public interface SubTypeHandler {
    /**
     * 获取子消息类型
     * @author wangjinfei
     * @date 2025/6/2 19:09
     * @return String
     */
    String getSubType();

    /**
     * 处理消息
     * @author wangjinfei
     * @date 2025/6/2 19:09
     * @param chatMessageDTO
    */
    void handle(ChatMessageDTO chatMessageDTO);

    /**
     * 获取主消息类型
     * @author wangjinfei
     * @date 2025/6/2 20:03
     * @return String
    */
    String getMessageType();
}
