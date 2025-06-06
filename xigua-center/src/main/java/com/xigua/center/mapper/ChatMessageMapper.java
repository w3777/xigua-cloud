package com.xigua.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.vo.ChatMessageVO;
import com.xigua.domain.vo.LastChatVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName Message
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/17 19:16
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    /**
     * 获取好友最后一条消息
     * @author wangjinfei
     * @date 2025/5/19 21:26
     * @param page
     * @return List<LastChatVO>
    */
    List<LastChatVO> getFriendLastMes(Page<LastChatVO> page, @Param("userId") String userId);

    /**
     * 分页获取好友历史消息
     * @author wangjinfei
     * @date 2025/5/25 11:01
     * @param page
     * @param senderId
     * @param receiverId
     * @return List<ChatMessageVO>
    */
    List<ChatMessageVO> getHistoryMes(Page<ChatMessageVO> page, @Param("senderId") String senderId,
                                      @Param("receiverId") String receiverId);

    /**
     * 批量标记消息为已读
     * @author wangjinfei
     * @date 2025/6/4 21:58
     * @param ids
     * @param updateBy
     * @return Integer
     */
    Integer batchRead(@Param("ids") List<String> ids, @Param("updateBy")String updateBy);
}
