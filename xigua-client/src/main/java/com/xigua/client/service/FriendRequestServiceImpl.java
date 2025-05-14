package com.xigua.client.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.client.mapper.FriendRequestMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.domain.entity.FriendRequest;
import com.xigua.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * @ClassName FriendRequestServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/13 21:22
 */
@DubboService
@RequiredArgsConstructor
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest> implements FriendRequestService {

    /**
     * 查询发送的好友请求
     * @author wangjinfei
     * @date 2025/5/13 23:34
     * @param userId
     * @return List<FriendRequest>
     */
    @Override
    public List<FriendRequest> getListBySenderId(String userId) {
        List<FriendRequest> friendRequestList = baseMapper.selectList(new LambdaQueryWrapper<FriendRequest>()
                .eq(FriendRequest::getSenderId, userId));
        return friendRequestList;
    }

    /**
     * 查询接收的好友请求
     * @author wangjinfei
     * @date 2025/5/13 23:34
     * @param userId
     * @return List<FriendRequest>
     */
    @Override
    public List<FriendRequest> getListByReceiverId(String userId) {
        List<FriendRequest> friendRequestList = baseMapper.selectList(new LambdaQueryWrapper<FriendRequest>()
                .eq(FriendRequest::getReceiverId, userId));
        return friendRequestList;
    }

    /**
     * 检查好友请求
     * @author wangjinfei
     * @date 2025/5/14 21:52
     * @param userId
     * @param friendId
     */
    @Override
    public void checkFriendRequest(String userId, String friendId) {
        Long friendRequestCount = baseMapper.selectCount(new LambdaQueryWrapper<FriendRequest>()
                .eq(FriendRequest::getSenderId, userId)
                .eq(FriendRequest::getReceiverId, friendId));
        if(friendRequestCount > 0){
            throw new BusinessException("您已经发送过好友请求，等待对方验证");
        }
    }

    /**
     * 修改好友请求状态
     * @author wangjinfei
     * @date 2025/5/14 22:13
     * @param friendRequest
     * @return Boolean
     */
    @Override
    public Boolean updateFriendRequestStatus(FriendRequest friendRequest) {
        int i = baseMapper.updateById(friendRequest);
        return i > 0;
    }

    /**
     * 根据发送者ID、接收者ID和状态查询好友请求
     * @author wangjinfei
     * @date 2025/5/14 22:16
     * @param senderId
     * @param receiverId
     * @param status
     * @return FriendRequest
     */
    @Override
    public FriendRequest getBySenderIdAndReceiverIdAndStatus(String senderId, String receiverId, Integer status) {
        FriendRequest friendRequest = baseMapper.selectOne(new LambdaQueryWrapper<FriendRequest>()
                .eq(FriendRequest::getSenderId, receiverId)
                .eq(FriendRequest::getReceiverId, senderId)
                .eq(FriendRequest::getStatus, status));
        return friendRequest;
    }
}
