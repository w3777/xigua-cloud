package com.xigua.center.message;

import com.xigua.domain.dto.ChatMessageDTO;

/**
 * @ClassName AbstractMessageService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/14 21:41
 */
public abstract class AbstractMessageService {
    /**
     * 处理消息
     * @author wangjinfei
     * @date 2025/8/14 21:52
     * @param chatMessageDTO
    */
    public abstract void handleMessage(ChatMessageDTO chatMessageDTO);

    /**
     * 获取消息服务名称
     * @author wangjinfei
     * @date 2025/8/14 22:31
     * @return String
    */
    public abstract String getMessageName();

    /**
     * 获取消息主类型
     * @author wangjinfei
     * @date 2025/8/14 22:27
     * @return String
    */
    public abstract String getMessageType();

    /**
     * 获取消息子类型
     * @author wangjinfei
     * @date 2025/8/14 22:27
     * @return String
    */
    public abstract String getMessageSubType();
}
