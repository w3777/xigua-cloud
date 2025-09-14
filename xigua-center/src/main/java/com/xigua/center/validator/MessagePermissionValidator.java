package com.xigua.center.validator;

import com.xigua.domain.enums.ChatType;
import com.xigua.domain.ws.MessageRequest;

/**
 * @ClassName MessagePermissionValidator
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/13 17:31
 */
public interface MessagePermissionValidator {
    /**
     * 支持的聊天类型
     */
    ChatType supportChatType();

    /**
     * 校验消息发送权限
     */
    ChatValidateCode validatePermission(MessageRequest request);

    /**
     * 校验消息内容
     */
//    void validateContent(MessageRequest request);

    /**
     * 校验发送频率
     */
//    void validateRateLimit(MessageRequest request);
}
