package com.xigua.center.validator;

import com.xigua.api.service.FriendRelationService;
import com.xigua.domain.enums.ChatType;
import com.xigua.domain.ws.MessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @ClassName PrivateChatValidator
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/13 17:35
 */
@Component
@Slf4j
public class PrivateChatValidator implements MessagePermissionValidator {
    @DubboReference
    private FriendRelationService friendRelationService;

    @Override
    public ChatType supportChatType() {
        return ChatType.ONE;
    }

    @Override
    public ChatValidateCode validatePermission(MessageRequest request) {
        Boolean isFriend = friendRelationService.isFriend(request.getReceiverId(), request.getSenderId());
        if (!isFriend) {
            return ChatValidateCode.NOT_FRIEND;
        }

        return ChatValidateCode.SUCCESS;
    }
}
