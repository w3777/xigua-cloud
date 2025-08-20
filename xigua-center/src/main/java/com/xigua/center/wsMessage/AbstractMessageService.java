package com.xigua.center.wsMessage;

import com.xigua.domain.ws.MessageRequest;

/**
 * @ClassName AbstractMessageService
 * @Description websocket 消息抽象类
 * @Author wangjinfei
 * @Date 2025/8/14 21:41
 */
public abstract class AbstractMessageService {
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

    /**
     * 处理消息
     * @author wangjinfei
     * @date 2025/8/14 21:52
     * @param messageRequest
    */
    public abstract void handleMessage(MessageRequest messageRequest);
}
