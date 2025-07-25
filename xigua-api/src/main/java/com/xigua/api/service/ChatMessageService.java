package com.xigua.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.dto.GetLastMesDTO;
import com.xigua.domain.dto.GetHistoryMes;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.result.BasePageVO;
import com.xigua.domain.vo.ChatMessageVO;
import com.xigua.domain.vo.LastMessageVO;

import java.util.List;

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
    BasePageVO<LastMessageVO> getLastMes(GetLastMesDTO dto);

    /**
     * 分页获取好友历史消息
     * @author wangjinfei
     * @date 2025/5/25 10:16
     * @param dto
     * @return BasePageVO<ChatMessageVO>
    */
    BasePageVO<ChatMessageVO> getHistoryMes(GetHistoryMes dto);

    /**
     * 批量标记消息为已读
     * @author wangjinfei
     * @date 2025/6/4 21:58
     * @param ids
     * @param updateBy
     * @return Integer
    */
    Integer batchRead(List<String> ids, String updateBy);
}
