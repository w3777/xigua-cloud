package com.xigua.center.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.center.mapper.ChatMessageMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.domain.dto.GetFriendLastMesDTO;
import com.xigua.domain.dto.GetHistoryMes;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.domain.result.BasePageVO;
import com.xigua.domain.util.BasePage;
import com.xigua.domain.vo.ChatMessageVO;
import com.xigua.domain.vo.LastChatVO;
import com.xigua.service.CenterService;
import com.xigua.service.ChatMessageService;
import com.xigua.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

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
@DubboService
@RequiredArgsConstructor
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
    private final RedisUtil redisUtil;
    @DubboReference
    private final UserService userService;
    @DubboReference
    private final CenterService centerService;

    /**
     * 获取好友最后一条消息
     * @author wangjinfei
     * @date 2025/5/17 19:25
     * @param dto
     * @return List<LastChatVO>
     */
    @Override
    public BasePageVO<LastChatVO> getFriendLastMes(GetFriendLastMesDTO dto) {
        String topUserId = dto.getTopUserId();
        String userId = UserContext.get().getUserId();
        Integer pageNum = dto.getPageNum();
        Integer pageSize = dto.getPageSize();
        List<LastChatVO> lastChatList = new ArrayList<>();

        // 从redis查询最后一条消息好友
        String lastFriendKey = RedisEnum.LAST_MES_FRIEND.getKey() + userId;
        Long totalCount = redisUtil.zsetSize(lastFriendKey);
        Set<Object> lastFriends = redisUtil.zsReverseRange(lastFriendKey, 0, 9);
        lastFriends.removeIf(friendId -> friendId.equals(topUserId));

        // 置顶用户
        if(StringUtils.isNotEmpty(topUserId)){
            getTopUser(topUserId, lastChatList);
        }

        // 遍历好友最后一条消息
        for (Object lastFriend : lastFriends) {
            LastChatVO lastChatVO = new LastChatVO();
            String friendId = lastFriend.toString();
            lastChatVO.setUserId(friendId);

            // todo 优化成从redis中获取用户信息
            User friend = userService.getById(friendId);
            lastChatVO.setUsername(friend.getUsername());
            lastChatVO.setAvatar(friend.getAvatar());

            Object mesObj = redisUtil.hashGet(RedisEnum.LAST_MES.getKey() + userId, friendId);
            if (mesObj != null) {
                JSONObject lastFriendMes = JSONObject.parseObject(mesObj.toString());
                lastChatVO.setLastMessage(lastFriendMes.getString("message"));
            }else{
                lastChatVO.setLastMessage("");
            }
            lastChatVO.setIsOnline(centerService.isOnline(lastFriend.toString()));
            lastChatList.add(lastChatVO);
        }

        // 封装分页
        BasePageVO<LastChatVO> result = new BasePageVO<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setRows(lastChatList);
        result.setTotalPage((totalCount + pageSize - 1) / pageSize);
        result.setTotalCount(totalCount);

        return result;
    }

    /**
     * 获取置顶用户
     * @author wangjinfei
     * @date 2025/5/20 22:23
     * @param topUserId
     * @param lastChatList
    */
    public void getTopUser(String topUserId, List<LastChatVO> lastChatList){
        String userId = UserContext.get().getUserId();
        User topUser = userService.getById(topUserId);
        if (topUser == null) {
            throw new BusinessException("置顶用户不存在");
        }

        // 置顶用户
        // todo 可以优化成从redis获取
        LastChatVO topLastChatVO = new LastChatVO();
        topLastChatVO.setUserId(topUserId);
        topLastChatVO.setUsername(topUser.getUsername());
        topLastChatVO.setAvatar(topUser.getAvatar());

        // 获取置顶用户最后一条消息
        Object mesObj = redisUtil.hashGet(RedisEnum.LAST_MES.getKey() + userId, topUserId);
        if (mesObj != null) {
            JSONObject lastFriendMes = JSONObject.parseObject(mesObj.toString());
            topLastChatVO.setLastMessage(lastFriendMes.getString("message"));
        }else{
            topLastChatVO.setLastMessage("");
        }

        Boolean online = centerService.isOnline(topUserId);
        topLastChatVO.setIsOnline(online);
        lastChatList.add(topLastChatVO);
    }

    /**
     * 分页获取好友历史消息
     * @author wangjinfei
     * @date 2025/5/25 10:16
     * @param dto
     * @return BasePageVO<ChatMessageVO>
     */
    @Override
    public BasePageVO<ChatMessageVO> getHistoryMes(GetHistoryMes dto) {
        String senderId = UserContext.get().getUserId();
        String receiverId = dto.getReceiverId();
        Integer pageNum = dto.getPageNum();
        Integer pageSize = dto.getPageSize();
        Page<ChatMessageVO> page = new Page<>(pageNum, pageSize);
        List<ChatMessageVO> chatMesList = new ArrayList<>();
        // 查询历史消息（我发给好友和好友发给我的消息，按时间倒序查询）
        chatMesList = baseMapper.getHistoryMes(page, senderId, receiverId);
        // 反转消息列表 页面显示最新的消息在最下面
        Collections.reverse(chatMesList);
        BasePageVO<ChatMessageVO> result = BasePage.getResult(page, chatMesList);

        return result;
    }

    /**
     * 批量标记消息为已读
     * @author wangjinfei
     * @date 2025/6/4 21:58
     * @param ids
     * @param updateBy
     * @return Integer
     */
    @Override
    public Integer batchRead(List<String> ids, String updateBy) {
        return baseMapper.batchRead(ids, updateBy);
    }
}
