package com.xigua.center.controller;

import com.xigua.api.service.SyncRedisService;
import com.xigua.domain.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @ClassName SyncRedisController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/9 12:18
 */
@Tag(name = "同步redis接口")
@RestController
@RequestMapping("/sync/redis")
public class SyncRedisController {
    @Autowired
    private SyncRedisService redisService;

    /**
     * 同步用户到redis
     * @author wangjinfei
     * @date 2025/7/29 17:20
     * @param params
     * @return Boolean
     */
    @Operation(summary = "同步用户到redis")
    @PostMapping("/syncUser2Redis")
    public R<Boolean> syncUser2Redis(@RequestBody Map<String, Object> params) {
        List<String> userIds = (List<String>) params.get("userIds");
        if(CollectionUtils.isNotEmpty(userIds)){
            return R.ok(redisService.syncUser2Redis(userIds));
        }
        return R.ok(redisService.syncUser2Redis(null));
    }

    /**
     * 同步群组到redis
     * @author wangjinfei
     * @date 2025/7/29 17:49
     * @param params
     * @return Boolean
     */
    @Operation(summary = "同步群组到redis")
    @PostMapping("/syncGroup2Redis")
    public R<Boolean> syncGroup2Redis(@RequestBody Map<String, Object> params){
        List<String> groupIds = (List<String>) params.get("groupIds");
        if(CollectionUtils.isNotEmpty(groupIds)){
            return R.ok(redisService.syncGroup2Redis(groupIds));
        }
        return R.ok(redisService.syncGroup2Redis(null));
    }

    /**
     * 同步聊天列表到redis
     * @author wangjinfei
     * @date 2025/8/9 11:48
     * @param params
     * @return Boolean
     */
    @Operation(summary = "同步聊天列表到redis")
    @PostMapping("/syncChatList2Redis")
    public R<Boolean> syncChatList2Redis(@RequestBody Map<String, Object> params){
        List<String> userIds = (List<String>) params.get("userIds");
        if(CollectionUtils.isNotEmpty(userIds)){
            return R.ok(redisService.syncChatList2Redis(userIds));
        }
        return R.ok(redisService.syncChatList2Redis(null));
    }

    /**
     * 同步群组聊天列表到redis
     * @author wangjinfei
     * @date 2025/8/17 9:40
     * @param params
     * @return Boolean
     */
    @Operation(summary = "同步群组聊天列表到redis")
    @PostMapping("/syncGroupChatList2Redis")
    public R<Boolean> syncGroupChatList2Redis(@RequestBody Map<String, Object> params){
        List<String> groupIds = (List<String>) params.get("groupIds");
        if(CollectionUtils.isNotEmpty(groupIds)){
            return R.ok(redisService.syncGroupChatList2Redis(groupIds));
        }
        return R.ok(redisService.syncGroupChatList2Redis(null));
    }
}
