package com.xigua.client.service;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.api.service.*;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.domain.entity.FriendRequest;
import com.xigua.domain.entity.Group;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.domain.vo.ContactCountVO;
import com.xigua.domain.vo.FriendRequestVO;
import com.xigua.domain.vo.FriendVO;
import com.xigua.domain.vo.GroupVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        // 发送好友申请数量
        contactCountVO.setSendCount(friendRequestService.getSendCountByUserId(userId));
        // 接收好友申请数量
        contactCountVO.setReceiveCount(friendRequestService.getReceiveCountByUserId(userId));

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

    /**
     * 获取发送好友申请列表
     * @author wangjinfei
     * @date 2025/8/2 12:35
     * @return List<FriendRequestVO>
     */
    @Override
    public List<FriendRequestVO> getSendFriendRequestList() {
        String userId = UserContext.get().getUserId();
        List<FriendRequestVO> voList = new ArrayList<>();

        // 查询发送的好友请求
        List<FriendRequest> senderFriendReqList = friendRequestService.getListBySenderId(userId);
        if(CollectionUtils.isEmpty(senderFriendReqList)){
            return voList;
        }

        // 映射发送好友请求vo字段
        for (FriendRequest friendRequest : senderFriendReqList) {
            String receiverId = friendRequest.getReceiverId();

            // 从redis获取用户信息
            String friendCache = redisUtil.get(RedisEnum.USER.getKey() + receiverId);
            if(StringUtils.isEmpty(friendCache)){
                continue;
            }
            User receiver = JSONObject.parseObject(friendCache, User.class);
            if(receiver == null){
                continue;
            }

            // 映射vo字段
            FriendRequestVO friendRequestVO = new FriendRequestVO();
            friendRequestVO.setRequestId(friendRequest.getId());
            friendRequestVO.setUserId(receiver.getId());
            friendRequestVO.setUsername(receiver.getUsername());
            friendRequestVO.setAvatar(receiver.getAvatar());
            friendRequestVO.setApplyMsg(friendRequest.getApplyMsg());
            friendRequestVO.setSource("send");

            friendRequestVO.setFlowStatus(friendRequest.getFlowStatus());
            friendRequestVO.setCreateTime(DateUtil.formatDateTime(friendRequest.getCreateTime(),
                    DateUtil.DATE_TIME_FORMATTER));
            voList.add(friendRequestVO);
        }

        return voList;
    }

    /**
     * 获取接收好友申请列表
     * @author wangjinfei
     * @date 2025/8/2 12:35
     * @return List<FriendRequestVO>
     */
    @Override
    public List<FriendRequestVO> getReceiveFriendRequestList() {
        String userId = UserContext.get().getUserId();
        List<FriendRequestVO> voList = new ArrayList<>();

        // 查询接收的好友请求
        List<FriendRequest> receiverFriendReqList = friendRequestService.getListByReceiverId(userId);
        if(CollectionUtils.isEmpty(receiverFriendReqList)){
            return voList;
        }

        // 映射接收好友请求vo字段
        for (FriendRequest friendRequest : receiverFriendReqList) {
            String senderId = friendRequest.getSenderId();
            // 从redis获取用户信息
            String friendCache = redisUtil.get(RedisEnum.USER.getKey() + senderId);
            if(StringUtils.isEmpty(friendCache)){
                continue;
            }
            User sender = JSONObject.parseObject(friendCache, User.class);
            if(sender == null){
                continue;
            }
            FriendRequestVO friendRequestVO = new FriendRequestVO();
            friendRequestVO.setRequestId(friendRequest.getId());
            friendRequestVO.setUserId(sender.getId());
            friendRequestVO.setUsername(sender.getUsername());
            friendRequestVO.setAvatar(sender.getAvatar());
            friendRequestVO.setApplyMsg(friendRequest.getApplyMsg());
            friendRequestVO.setSource("receive");

            friendRequestVO.setFlowStatus(friendRequest.getFlowStatus());
            friendRequestVO.setCreateTime(DateUtil.formatDateTime(friendRequest.getCreateTime(),
                    DateUtil.DATE_TIME_FORMATTER));
            voList.add(friendRequestVO);
        }
        return voList;
    }
}
