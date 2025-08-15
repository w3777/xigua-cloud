package com.xigua.client.service;

import com.xigua.api.service.FriendRelationService;
import com.xigua.api.service.GroupMemberService;
import com.xigua.api.service.HomeService;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.domain.vo.HomeCountVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName HomeServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/9 10:54
 */
@Slf4j
@Service
@DubboService
public class HomeServiceImpl implements HomeService {
    @Autowired
    private FriendRelationService friendRelationService;
    @Autowired
    private GroupMemberService groupMemberService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取首页统计信息
     * @author wangjinfei
     * @date 2025/8/9 10:56
     * @return HomeCountVO
     */
    @Override
    public HomeCountVO getHomeCount() {
        String userId = UserContext.get().getUserId();
        HomeCountVO homeCountVO = new HomeCountVO();

        // 好友数量
        homeCountVO.setFriendCount(friendRelationService.getCountByUserId(userId));
        // 群数量
        homeCountVO.setGroupCount(groupMemberService.getCountByUserId(userId));

        // 未读消息
        // 好友未读消息
        Long unreadCount = redisUtil.hincrget(RedisEnum.FRIEND_UNREAD_COUNT.getKey() + userId);
        homeCountVO.setUnreadCount(unreadCount);
        return homeCountVO;
    }
}
