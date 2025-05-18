package com.xigua.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.vo.LastChatVO;

import java.util.List;

/**
 * @ClassName ChatMessageService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/17 19:18
 */
public interface ChatMessageService extends IService<ChatMessage> {
    /**
     * 获取最后几条聊天记录
     * @author wangjinfei
     * @date 2025/5/17 19:25
     * @param topUserId 置顶用户聊天记录的用户id
     * @return List<LastChatVO>
    */
    List<LastChatVO> getLastChat(String topUserId);
}
