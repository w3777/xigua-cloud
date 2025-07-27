package com.xigua.client.service;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.api.service.*;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.domain.entity.Group;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.domain.vo.ContactCountVO;
import com.xigua.domain.vo.FriendVO;
import com.xigua.domain.vo.GroupVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @ClassName ContactServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/27 9:38
 */
@Service
public class ContactServiceImpl implements ContactService {
    @Autowired
    private FriendRelationService friendRelationService;
    @Autowired
    private FriendRequestService friendRequestService;
    @Autowired
    private GroupMemberService groupMemberService;
    @Autowired
    private CenterService centerService;
    @Autowired
    private RedisUtil redisUtil;


    /**
     * 获取联系人数量
     * @author wangjinfei
     * @date 2025/7/27 9:42
     * @return ContactCountVO
     */
    @Override
    public ContactCountVO getContactCount() {
        String userId = UserContext.get().getUserId();
        ContactCountVO contactCountVO = new ContactCountVO();

        // 好友数量
        contactCountVO.setFriendCount(friendRelationService.getCountByUserId(userId));
        // 群数量
        contactCountVO.setGroupCount(groupMemberService.getCountByUserId(userId));
        // 好友请求数量
        contactCountVO.setFriendRequestCount(friendRequestService.getCountByUserId(userId));

        return contactCountVO;
    }

    /**
     * 获取好友列表
     * @author wangjinfei
     * @date 2025/5/14 20:56
     * @return List<FriendVO>
     */
    @Override
    public List<FriendVO> getFriendList() {
        // todo 优化成分页获取好友

        String userId = UserContext.get().getUserId();
        List<FriendVO> voList = new ArrayList<>();

        // 获取好友id列表
        List<String> friendIds = friendRelationService.getFriendIdsByUserId(userId);
        if(CollectionUtils.isEmpty(friendIds)){
            return voList;
        }

        // 映射vo字段
        for (String friendId : friendIds) {
            // todo 可以批量获取
            // 从redis获取用户信息
            String friendCache = redisUtil.get(RedisEnum.USER.getKey() + friendId);
            if(StringUtils.isEmpty(friendCache)){
                continue;
            }
            User user = JSONObject.parseObject(friendCache, User.class);

            FriendVO friendVO = new FriendVO();
            friendVO.setUserId(friendId);
            friendVO.setUsername(user.getUsername());
            friendVO.setAvatar(user.getAvatar());
            friendVO.setSignature(user.getSignature());

            // 判断是否在线
            friendVO.setIsOnline(centerService.isOnline(friendId));
            voList.add(friendVO);
        }
        return voList;
    }

    /**
     * 获取群列表
     * @author wangjinfei
     * @date 2025/7/27 11:44
     * @return List<GroupVO>
     */
    @Override
    public List<GroupVO> getGroupList() {
        // todo 优化成分页获取群组

        String userId = UserContext.get().getUserId();
        List<GroupVO> voList = new ArrayList<>();

        // 获取加入的群组
        Set<String> groupIds = groupMemberService.getGroupIdsByUserId(userId);
        if(CollectionUtils.isEmpty(groupIds)){
            return voList;
        }
        for (String groupId : groupIds) {
            String groupCache = redisUtil.get(RedisEnum.GROUP.getKey() + groupId);
            if(StringUtils.isEmpty(groupCache)){
                continue;
            }
            Group group = JSONObject.parseObject(groupCache, Group.class);
            GroupVO groupVO = new GroupVO();
            groupVO.setGroupId(group.getId());
            groupVO.setGroupName(group.getGroupName());
            groupVO.setGroupAvatar(group.getGroupAvatar());
            groupVO.setCurrentMember(group.getCurrentMember());
            voList.add(groupVO);
        }

        return voList;
    }
}
