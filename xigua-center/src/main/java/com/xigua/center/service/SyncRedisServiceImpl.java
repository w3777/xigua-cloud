package com.xigua.center.service;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.api.service.*;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.bo.LastMessageBO;
import com.xigua.domain.bo.LastMessageContentBO;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.ChatType;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.domain.vo.LastMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SyncRedisServiceImpl
 * @Description
 * @Author wangjinfei
 * @Date 2025/7/29 17:01
 */
@Slf4j
@Service
@DubboService
public class SyncRedisServiceImpl implements SyncRedisService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ChatMessageService chatMessageService;
    @DubboReference
    private UserService userService;
    @DubboReference
    private GroupService groupService;
    @DubboReference
    private FriendRelationService friendRelationService;

    /**
     * 同步用户到redis
     * @author wangjinfei
     * @date 2025/7/29 17:20
     * @param userIds
     * @return Boolean
     */
    @Override
    public Boolean syncUser2Redis(List<String> userIds) {
        List<String> needSyncUserIds = new ArrayList<>();

        // 有用户id列表，直接同步
        if(CollectionUtils.isNotEmpty(userIds)){
            needSyncUserIds = userIds;
        }else{
            // 没有用户id列表，查询所有用户id
            needSyncUserIds = userService.getAllUserId();
        }

        if(CollectionUtils.isEmpty(needSyncUserIds)){
            return false;
        }

        // 同步用户到redis
        for (String userId : needSyncUserIds) {
            userService.addUser2Redis(userId);
        }

        return true;
    }

    /**
     * 同步群组到redis
     * @author wangjinfei
     * @date 2025/7/29 17:49
     * @param groupIds
     * @return Boolean
     */
    @Override
    public Boolean syncGroup2Redis(List<String> groupIds) {
        List<String> needSyncGroupIds = new ArrayList<>();

        // 有群组id列表，直接同步
        if(CollectionUtils.isNotEmpty(groupIds)){
            needSyncGroupIds = groupIds;
        }else{
            // 没有群组id列表，查询所有群组id
            needSyncGroupIds = groupService.getAllGroupId();
        }

        if(CollectionUtils.isEmpty(needSyncGroupIds)){
            return false;
        }

        // 同步群组到redis
        for (String groupId : needSyncGroupIds) {
            groupService.addGroup2Redis(groupId);
        }

        return true;
    }

    /**
     * 同步聊天列表到redis
     * @author wangjinfei
     * @date 2025/8/9 11:48
     * @param userIds
     * @return Boolean
     */
    @Override
    public Boolean syncChatList2Redis(List<String> userIds) {
        List<String> needSyncUserIds = new ArrayList<>();

        // 有用户id列表，直接同步
        if(CollectionUtils.isNotEmpty(userIds)){
            needSyncUserIds = userIds;
        }else{
            // 没有用户id列表，查询所有用户id
            needSyncUserIds = userService.getAllUserId();
        }

        if(CollectionUtils.isEmpty(needSyncUserIds)){
            return false;
        }

        // 遍历用户
        for (String userId : needSyncUserIds) {
            List<User> friends = friendRelationService.getFriendsByUserId4Db(userId);
            if(CollectionUtils.isEmpty(friends)){
                continue;
            }

            for (User friend : friends) {
                String friendId = friend.getId();

                // 获取好友最后一条消息
                ChatMessage lastMessage = chatMessageService.getLastMessage(friendId, userId);
                if(lastMessage == null){
                    continue;
                }

                // 映射vo
                LastMessageVO lastMessageVO = new LastMessageVO();
                lastMessageVO.setChatId(friendId);
                lastMessageVO.setChatType(ChatType.ONE.getType());
                lastMessageVO.setChatName(friend.getUsername());
                lastMessageVO.setAvatar(friend.getAvatar());
                LastMessageContentBO lastMessageContentBO = new LastMessageContentBO();
                lastMessageContentBO.setContent(lastMessage.getMessage());
                lastMessageVO.setLastMessageContent(lastMessageContentBO);
                lastMessageVO.setUpdateTime(DateUtil.toEpochMilli(lastMessage.getCreateTime()));

                // 同步redis 最后消息
                redisUtil.zsadd(RedisEnum.LAST_MES.getKey() + userId, friendId, DateUtil.toEpochMilli(lastMessage.getCreateTime()));
                // 同步redis 最后消息内容
                redisUtil.hashPut(RedisEnum.LAST_MES_CONTENT.getKey() + userId, friendId, JSONObject.toJSONString(lastMessageVO));
            }
        }

        return true;
    }
}
