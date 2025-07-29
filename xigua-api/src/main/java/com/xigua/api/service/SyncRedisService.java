package com.xigua.api.service;

import java.util.List;

/**
 * @ClassName SyncRedisService
 * @Description
 * @Author wangjinfei
 * @Date 2025/7/29 17:01
 */
public interface SyncRedisService {

    /**
     * 同步用户到redis
     * @author wangjinfei
     * @date 2025/7/29 17:20
     * @param userIds
     * @return Boolean
    */
    Boolean syncUser2Redis(List<String> userIds);

    /**
     * 同步群组到redis
     * @author wangjinfei
     * @date 2025/7/29 17:49
     * @param groupIds
     * @return Boolean
    */
    Boolean syncGroup2Redis(List<String> groupIds);
}
