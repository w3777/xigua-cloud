package com.xigua.client.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.client.mapper.ChatMessageMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.UserContext;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.entity.User;
import com.xigua.domain.vo.LastChatVO;
import com.xigua.service.CenterService;
import com.xigua.service.ChatMessageService;
import com.xigua.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ChatMessageServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/17 19:19
 */
@DubboService
@RequiredArgsConstructor
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
    @DubboReference
    private final UserService userService;
    @DubboReference
    private final CenterService centerService;

    /**
     * 获取最后几条聊天记录
     * @author wangjinfei
     * @date 2025/5/17 19:25
     * @param topUserId 置顶用户聊天记录的用户id
     * @return List<LastChatVO>
     */
    @Override
    public List<LastChatVO> getLastChat(String topUserId) {
        String userId = UserContext.get().getUserId();
        List<LastChatVO> lastChatList = new ArrayList<>();
        User topUser = userService.getById(topUserId);
        if (topUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 置顶用户的聊天记录
        LastChatVO topLastChatVO = new LastChatVO();
        topLastChatVO.setUserId(topUserId);
        topLastChatVO.setUsername(topUser.getUsername());
        topLastChatVO.setAvatar(topUser.getAvatar());
        topLastChatVO.setLastMessage("");

        Boolean online = centerService.isOnline(topUserId);
        topLastChatVO.setIsOnline(online);
        lastChatList.add(topLastChatVO);


        // todo 分页查询剩余几天的聊天记录
        return lastChatList;
    }
}
