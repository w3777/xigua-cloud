package com.xigua.client.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.client.mapper.FriendRequestMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.domain.entity.FriendRequest;
import com.xigua.domain.enums.FriendRequestFlowStatus;
import com.xigua.domain.enums.FriendRequestStatus;
import com.xigua.api.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName FriendRequestServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/13 21:22
 */
@Slf4j
@Service
@DubboService
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
     * 修改好友请求流程状态为失效
     * @author wangjinfei
     * @date 2025/5/14 22:13
     * @param requestId
     * @return Boolean
     */
    @Override
    public Boolean updateFlowStatus(String requestId, FriendRequestFlowStatus flowStatus) {
        int i = baseMapper.update(new LambdaUpdateWrapper<FriendRequest>()
                .eq(FriendRequest::getId, requestId)
                .set(FriendRequest::getFlowStatus, flowStatus.getStatus())
                .set(FriendRequest::getUpdateTime, LocalDateTime.now()));
        return i > 0;
    }

    /**
     * 根据发送者ID、接收者ID获取最后一条好友请求
     * @author wangjinfei
     * @date 2025/5/14 22:16
     * @param senderId
     * @param receiverId
     * @return FriendRequest
     */
    @Override
    public FriendRequest getLastOne(String senderId, String receiverId) {
        FriendRequest friendRequest = baseMapper.selectOne(new LambdaQueryWrapper<FriendRequest>()
                .eq(FriendRequest::getSenderId, senderId)
                .eq(FriendRequest::getReceiverId, receiverId)
                .orderByDesc(FriendRequest::getCreateTime)
                .last("limit 1"));
        return friendRequest;
    }

    /**
     * 获取我发送的好友申请
     * @author wangjinfei
     * @date 2025/7/27 9:42
     * @param userId
     * @return Integer
     */
    @Override
    public Integer getSendCountByUserId(String userId) {
        Integer count = baseMapper.getSendCountByUserId(userId);
        return count == null ? 0 : count;
    }

    /**
     * 获取我接收的好友申请
     * @author wangjinfei
     * @date 2025/8/2 12:16
     * @param userId
     * @return Integer
     */
    @Override
    public Integer getReceiveCountByUserId(String userId) {
        Integer count = baseMapper.getReceiveCountByUserId(userId);
        return count == null ? 0 : count;
    }
}
