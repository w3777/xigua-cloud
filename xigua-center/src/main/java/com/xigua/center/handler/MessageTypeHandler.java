package com.xigua.center.handler;

import com.xigua.domain.dto.ChatMessageDTO;

/**
 * @ClassName MessageTypeHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/2 17:39
 */
public interface MessageTypeHandler {
    /**
     * 获取主消息类型
     * @author wangjinfei
     * @date 2025/6/2 19:02
     * @return String
    */
    String getMessageType();
    
    /** 
     * 根据子类型指定对应的消息处理
     * @author wangjinfei
     * @date 2025/6/2 19:03 
     * @param subType 
    */
    void dispatchSub(String subType, ChatMessageDTO chatMessageDTO);
}
