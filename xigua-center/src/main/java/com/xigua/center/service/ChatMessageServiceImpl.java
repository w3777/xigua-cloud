package com.xigua.center.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.center.mapper.ChatMessageMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.domain.bo.SenderBO;
import com.xigua.domain.dto.GetLastMesDTO;
import com.xigua.domain.dto.GetHistoryMes;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.ChatType;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.domain.result.BasePageVO;
import com.xigua.domain.util.BasePage;
import com.xigua.domain.vo.ChatMessageVO;
import com.xigua.domain.vo.LastMessageVO;
import com.xigua.api.service.CenterService;
import com.xigua.api.service.ChatMessageService;
import com.xigua.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @ClassName ChatMessageServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/17 19:19
 */
@Slf4j
@Service
@DubboService
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CenterService centerService;
    @DubboReference
    private UserService userService;

    /**
     * 获取好友最后一条消息
     * @author wangjinfei
     * @date 2025/5/17 19:25
     * @param dto
     * @return List<LastChatVO>
     */
    @Override
    public BasePageVO<LastMessageVO> getLastMes(GetLastMesDTO dto) {
        String userId = UserContext.get().getUserId();
        Integer pageNum = dto.getPageNum();
        Integer pageSize = dto.getPageSize();
        List<LastMessageVO> lastChatList = new ArrayList<>();

        // todo 后续 可以扩展置顶消息

        // 从redis查询最后聊天消息
        String lastMesKey = RedisEnum.LAST_MES.getKey() + userId;
        Long totalCount = redisUtil.zsetSize(lastMesKey);
        long start = (pageNum - 1) * pageSize;
        long end = start + pageSize - 1;
        Set<Object> lastMessages = redisUtil.zsReverseRange(lastMesKey, start, end);

        // 遍历最后聊天消息 （获取内容、在线状态等）
        for (Object lastMessage : lastMessages) {
            Long unreadCount = 0L;
            Boolean isOnline = false;

            // 获取最后一条消息
            Object mesObj = redisUtil.hashGet(RedisEnum.LAST_MES_CONTENT.getKey() + userId, lastMessage.toString());

            // 获取最后一条消息的内容
            // LastMessageVO只在LastMessageBO字段上只扩展字段，其他字段相同，就直接用LastMessageVO接收
            LastMessageVO lastMessageVO = JSONObject.parseObject(mesObj.toString(), LastMessageVO.class);
            Integer chatType = lastMessageVO.getChatType();
            String senderId = lastMessageVO.getChatId();

            // 聊天类型
            if (chatType == ChatType.ONE.getType()) {
                // 好友是否在线
                isOnline = centerService.isOnline(senderId);
                // 好友未读消息数量
                unreadCount = redisUtil.hincrget(RedisEnum.FRIEND_UNREAD_COUNT.getKey() + userId, senderId);
            }else if(chatType == ChatType.TWO.getType()){
                isOnline = false;
                // 群聊未读消息数量
                unreadCount = redisUtil.hincrget(RedisEnum.GROUP_UNREAD_COUNT.getKey() + userId, senderId);
            }else {
                isOnline = false;
                unreadCount = 0L;
            }
            lastMessageVO.setIsOnline(isOnline);
            lastMessageVO.setUnreadCount(unreadCount);
            lastChatList.add(lastMessageVO);
        }

        // 封装分页
        BasePageVO<LastMessageVO> result = new BasePageVO<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setRows(lastChatList);
        result.setTotalPage((totalCount + pageSize - 1) / pageSize);
        result.setTotalCount(totalCount);

        return result;
    }

    /**
     * 获取置顶用户
     * fixme 暂时不考虑置顶消息
     * @author wangjinfei
     * @date 2025/5/20 22:23
     * @param topUserId
     * @param lastChatList
    */
//    public void getTopUser(String topUserId, List<LastMessageVO> lastChatList){
//        String userId = UserContext.get().getUserId();
//        User topUser = userService.getById(topUserId);
//        if (topUser == null) {
//            throw new BusinessException("置顶用户不存在");
//        }
//
//        // 置顶用户
//        // todo 可以优化成从redis获取
//        LastMessageVO topLastMessageVO = new LastMessageVO();
//        topLastMessageVO.setUserId(topUserId);
//        topLastMessageVO.setUsername(topUser.getUsername());
//        topLastMessageVO.setAvatar(topUser.getAvatar());
//
//        // 获取置顶用户最后一条消息
//        Object mesObj = redisUtil.hashGet(RedisEnum.LAST_MES.getKey() + userId, topUserId);
//        if (mesObj != null) {
//            JSONObject lastFriendMes = JSONObject.parseObject(mesObj.toString());
//            topLastMessageVO.setLastMessage(lastFriendMes.getString("message"));
//        }else{
//            topLastMessageVO.setLastMessage("");
//        }
//
//        Boolean online = centerService.isOnline(topUserId);
//        topLastMessageVO.setIsOnline(online);
//        lastChatList.add(topLastMessageVO);
//    }

    /**
     * 分页获取好友历史消息
     * @author wangjinfei
     * @date 2025/5/25 10:16
     * @param dto
     * @return BasePageVO<ChatMessageVO>
     */
    @Override
    public BasePageVO<ChatMessageVO> getHistoryMes(GetHistoryMes dto) {
        String userId = UserContext.get().getUserId();
        String receiverId = dto.getReceiverId();
        Integer chatType = dto.getChatType();
        Integer pageNum = dto.getPageNum();
        Integer pageSize = dto.getPageSize();
        Page<ChatMessageVO> page = new Page<>(pageNum, pageSize);
        List<ChatMessageVO> chatMesList = new ArrayList<>();
        // 好友聊天
        if (chatType == ChatType.ONE.getType()) {
            // 查询历史消息（我发给好友和好友发给我的消息，按时间倒序查询）
            chatMesList = baseMapper.getPrivateChatHistoryMes(page, userId, receiverId);
        }else if(chatType == ChatType.TWO.getType()){
            chatMesList = baseMapper.getGroupChatHistoryMes(page, receiverId);
        }else{
            throw new BusinessException("聊天类型不存在");
        }

        // 返回空分页
        if(CollectionUtils.isEmpty(chatMesList)){
            return BasePage.emptyResult(page);
        }

        // 群聊
        if(chatType == ChatType.TWO.getType()){
            // 遍历发送人信息
            for (ChatMessageVO chatMessageVO : chatMesList) {
                SenderBO senderBo = new SenderBO();
                String senderId = chatMessageVO.getSenderId();
                String userCache = redisUtil.get(RedisEnum.USER.getKey() + senderId);
                if(StringUtils.isNotEmpty(userCache)){
                    User user = JSONObject.parseObject(userCache, User.class);
                    senderBo.setUsername(user.getUsername());
                    senderBo.setAvatar(user.getAvatar());
                }
                chatMessageVO.setSender(senderBo);
            }
        }

        // 反转消息列表 页面显示最新的消息在最下面
        Collections.reverse(chatMesList);
        BasePageVO<ChatMessageVO> result = BasePage.getResult(page, chatMesList);

        return result;
    }

    /**
     * 获取好友最后一条消息
     * @author wangjinfei
     * @date 2025/8/9 12:11
     * @param senderId
     * @param receiverId
     * @return ChatMessage
     */
    @Override
    public ChatMessage getLastMessage(String senderId, String receiverId) {
        return baseMapper.getLastMessage(senderId, receiverId);
    }
}
