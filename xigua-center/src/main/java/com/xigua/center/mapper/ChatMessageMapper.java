package com.xigua.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.vo.ChatMessageVO;
import com.xigua.domain.vo.LastMessageVO;
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
    List<LastMessageVO> getLastMes(Page<LastMessageVO> page, @Param("userId") String userId);

    /**
     * 分页获取好友历史消息
     * @author wangjinfei
     * @date 2025/5/25 11:01
     * @param page
     * @param senderId
     * @param receiverId
     * @return List<ChatMessageVO>
    */
    List<ChatMessageVO> getPrivateChatHistoryMes(Page<ChatMessageVO> page, @Param("senderId") String senderId,
                                      @Param("receiverId") String receiverId);

    /**
     * 获取好友最后一条消息
     * @author wangjinfei
     * @date 2025/8/9 12:11
     * @param senderId
     * @param receiverId
     * @return ChatMessage
     */
    ChatMessage getLastMessage(@Param("senderId") String senderId, @Param("receiverId") String receiverId);

    /**
     * 分页获取群聊历史消息
     * @author wangjinfei
     * @date 2025/8/17 20:16
     * @param page
     * @param userId 当前用户id
     * @param groupId 群聊id
     * @return List<ChatMessageVO>
    */
    List<ChatMessageVO> getGroupChatHistoryMes(Page<ChatMessageVO> page, @Param("userId") String userId,
                                               @Param("groupId") String groupId);
}
