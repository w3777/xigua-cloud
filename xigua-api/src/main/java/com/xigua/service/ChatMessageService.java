package com.xigua.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.dto.GetFriendLastMesDTO;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.result.BasePageVO;
import com.xigua.domain.vo.LastChatVO;

/**
 * @ClassName ChatMessageService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/17 19:18
 */
public interface ChatMessageService extends IService<ChatMessage> {
    /**
     * 获取好友最后一条消息
     * @author wangjinfei
     * @date 2025/5/17 19:25
     * @param dto
     * @return List<LastChatVO>
    */
    BasePageVO<LastChatVO> getFriendLastMes(GetFriendLastMesDTO dto);
}
