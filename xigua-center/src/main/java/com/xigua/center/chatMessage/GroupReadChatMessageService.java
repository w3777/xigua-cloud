package com.xigua.center.chatMessage;

import com.xigua.api.service.CenterService;
import com.xigua.api.service.MessageReadService;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.enums.ChatType;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.domain.ws.MessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName GroupReadChatMessageService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/24 11:42
 */
@Component
public class GroupReadChatMessageService extends AbstractReadChatMessageService{
    @Autowired
    private MessageReadService messageReadService;
    @Autowired
    private CenterService centerService;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    protected ChatType getChatType() {
        return ChatType.TWO;
    }

    /**
     * 通知发送人消息已读
     * @author wangjinfei
     * @date 2025/8/24 11:32
     * @param messageRequest
     */
    @Override
    public void notifySenderOfMessageRead(MessageRequest messageRequest) {
        // 群聊不通知已读  以后可以扩展
        return;
    }

    /**
     * 清空未读消息数量
     * @author wangjinfei
     * @date 2025/8/24 11:35
     * @param messageRequest
     */
    @Override
    public void clearUnreadCount(MessageRequest messageRequest) {
        String senderId = messageRequest.getSenderId();
        String receiverId = messageRequest.getReceiverId();
        String groupUnreadCountKey = RedisEnum.GROUP_UNREAD_COUNT.getKey() + senderId;

        /**
         * todo 点开聊天框，清零，可以根据已读数据逐次 - 1
         * 暂时直接清零
         */
        redisUtil.hashPut(groupUnreadCountKey, receiverId, 0);
    }
}
