package com.xigua.center.service;

import com.xigua.api.service.GroupService;
import com.xigua.api.service.SyncRedisService;
import com.xigua.api.service.UserService;
import com.xigua.common.core.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
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
public class SyncRedisServiceImpl implements SyncRedisService {
    @Autowired
    private RedisUtil redisUtil;
    @DubboReference
    private UserService userService;
    @DubboReference
    private GroupService groupService;

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

        return null;
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

        return null;
    }
}
