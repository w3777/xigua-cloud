package com.xigua.client.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.client.mapper.FriendRelationMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.dto.FriendVerifyDTO;
import com.xigua.domain.dto.sendFriendRequestDTO;
import com.xigua.domain.entity.FriendRelation;
import com.xigua.domain.entity.FriendRequest;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.*;
import com.xigua.domain.vo.FriendDetailVO;
import com.xigua.domain.vo.FriendVO;
import com.xigua.domain.vo.FriendRequestVO;
import com.xigua.api.service.CenterService;
import com.xigua.api.service.FriendRelationService;
import com.xigua.api.service.FriendRequestService;
import com.xigua.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName FriendRelationServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/13 21:24
 */
@Service
@DubboService
@RequiredArgsConstructor
public class FriendRelationServiceImpl extends ServiceImpl<FriendRelationMapper, FriendRelation> implements FriendRelationService {
    private final Sequence sequence;
    private final FriendRequestService friendRequestService;
    private final UserService userService;
    private final CenterService centerService;
    private final RedisUtil redisUtil;

    /**
     * 发送好友请求
     * @author wangjinfei
     * @date 2025/5/13 21:26
     * @param dto
     * @return Boolean
     */
    @Override
    public Boolean sendFriendRequest(sendFriendRequestDTO dto) {
        String userId = UserContext.get().getUserId();
        String friendId = dto.getFriendId();
        // 检查好友关系
        checkFriendRelation(userId, friendId);

        // 添加好友申请
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(sequence.nextNo());
        friendRequest.setSenderId(userId);
        friendRequest.setReceiverId(friendId);
        friendRequest.setApplyMsg(dto.getApplyMsg());
        friendRequest.setFlowStatus(FriendRequestFlowStatus.ZERO.getStatus());
        boolean i = friendRequestService.save(friendRequest);

        return i;
    }

    /**
     * 检查好友关系
     * @author wangjinfei
     * @date 2025/5/13 21:24
     * @param userId
     * @param friendId
     * @return void
     */
    private void checkFriendRelation(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new BusinessException("不能添加自己为好友");
        }

        // 检查是否已经是好友关系
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<FriendRelation>()
                .eq(FriendRelation::getUserId, userId)
                .eq(FriendRelation::getFriendId, friendId));
        if (count > 0) {
            throw new BusinessException("已经是好友关系");
        }
    }

    /**
     * 好友验证
     * @author wangjinfei
     * @date 2025/5/14 22:00
     * @param dto
     * @return Boolean
     */
    @Override
    public Boolean friendVerify(FriendVerifyDTO dto) {
        String userId = UserContext.get().getUserId();
        String requestId = dto.getRequestId();
        String friendId = dto.getFriendId();
        Integer flowStatus = dto.getFlowStatus();

        // 验证是否存在好友关系
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<FriendRelation>()
                .eq(FriendRelation::getUserId, userId)
                .eq(FriendRelation::getFriendId, friendId));
        if(count > 0){
            throw new BusinessException("已经是好友关系");
        }

        if(flowStatus == FriendRequestFlowStatus.ONE.getStatus()){ // 同意
            // 双向关系 互相添加好友关系
            acceptFriendRequest(requestId, userId,friendId);
        }else if(flowStatus == FriendRequestFlowStatus.TWO.getStatus()){ // 拒绝
            rejectFriendRequest(requestId, userId, friendId);
        }else if(flowStatus == FriendRequestFlowStatus.ZERO.getStatus()){ // 待验证
            againFriendRequest(requestId, userId,friendId);
        }else {
            throw new BusinessException("好友请求状态异常");
        }

        return true;
    }

    /**
     * 同意好友请求
     * @author wangjinfei
     * @date 2025/5/14 22:10
     * @param userId
     * @param friendId
    */
    private void acceptFriendRequest(String requestId, String userId, String friendId) {
        // 把好友请求状态修改为同意
        friendRequestService.updateFlowStatus(requestId, FriendRequestFlowStatus.ONE);

        // 确认好友关系
        friendDbHandle(userId, friendId);
        // 角色反转 确认好友关系
        friendDbHandle(friendId, userId);

        // todo 可以走mq
        // 发送默认消息
        sendDefaultMessage(userId, friendId);
        // 发送默认消息
        sendDefaultMessage(friendId, userId);
    }

    /**
     * 好友db处理
     * 确认好友关系、添加默认消息
     * @author wangjinfei
     * @date 2025/5/21 11:42
     * @param userId
     * @param friendId
    */
    private void friendDbHandle(String userId, String friendId){
        // 添加好友关系
        FriendRelation friendRelation = new FriendRelation();
        friendRelation.setId(sequence.nextNo());
        friendRelation.setUserId(userId);
        friendRelation.setFriendId(friendId);
        boolean save = save(friendRelation);

        // 缓存好友关系
        redisUtil.zsadd(RedisEnum.FRIEND_RELATION.getKey() + userId, friendId, System.currentTimeMillis());
    }

    /**
     * 发送默认消息
     * @author wangjinfei
     * @date 2025/8/3 11:24
     * @param userId
     * @param friendId
    */
    private void sendDefaultMessage(String userId, String friendId){
        // 添加好友默认发送消息
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderId(userId);
        chatMessageDTO.setReceiverId(friendId);
        chatMessageDTO.setMessageType(MessageType.CHAT.getType());
        chatMessageDTO.setSubType(MessageSubType.MES_SEND.getType());
        chatMessageDTO.setMessage("你已成为我的好友");
        chatMessageDTO.setCreateTime(String.valueOf(System.currentTimeMillis()));
        centerService.receiveMessage4Client(chatMessageDTO);
    }

    /**
     * 拒绝好友请求
     * @author wangjinfei
     * @date 2025/5/14 22:11
     * @param userId
     * @param friendId
    */
    private void rejectFriendRequest(String requestId, String userId, String friendId){
        // 把好友请求状态修改为拒绝
        friendRequestService.updateFlowStatus(requestId, FriendRequestFlowStatus.TWO);
    }

    /**
     * 重新发送好友请求
     * @author wangjinfei
     * @date 2025/8/3 11:00
     * @param requestId
     * @param userId
     * @param friendId
    */
    private void againFriendRequest(String requestId, String userId, String friendId){
        // 把好友请求状态修改为待验证
        friendRequestService.updateFlowStatus(requestId, FriendRequestFlowStatus.ZERO);
    }

    /**
     * 好友详情
     * @author wangjinfei
     * @date 2025/5/17 12:06
     * @param friendId
     * @return FriendDetailVO
     */
    @Override
    public FriendDetailVO getFriendDetail(String friendId) {
        String userId = UserContext.get().getUserId();
        FriendDetailVO friendDetail = baseMapper.getFriendDetail(userId, friendId);
        friendDetail.setIsOnline(centerService.isOnline(friendDetail.getUserId()));
        return friendDetail;
    }

    /**
     * 所有好友关系存入redis
     * @author wangjinfei
     * @date 2025/6/2 20:58
     */
    @Override
    public void allFriendRelationToRedis() {
        List<User> userList = userService.list();
        for(User user : userList){
            String userId = user.getId();
            List<FriendRelation> friendList = baseMapper.selectList(new LambdaQueryWrapper<FriendRelation>()
                    .eq(FriendRelation::getUserId, userId));
            if(CollectionUtils.isEmpty(friendList)){
                continue;
            }

            for (FriendRelation friendRelation : friendList) {
                String friendId = friendRelation.getFriendId();
                // 缓存好友关系
                redisUtil.zsadd(RedisEnum.FRIEND_RELATION.getKey() + userId, friendId, System.currentTimeMillis());
            }
        }
    }

    /**
     * 获取好友数量
     * @author wangjinfei
     * @date 2025/7/27 9:42
     * @param userId
     * @return Integer
     */
    @Override
    public Integer getCountByUserId(String userId) {
        Integer count = baseMapper.getCountByUserId(userId);

        return count == null ? 0 : count;
    }

    /**
     * 获取好友id列表
     * @author wangjinfei
     * @date 2025/7/27 10:50
     * @param userId
     * @return List<String>
     */
    @Override
    public List<String> getFriendIdsByUserId(String userId) {
        List<String> friendIds = new ArrayList<>();

        // 从缓存中获取好友id列表
        friendIds = getFriendIdsByUserIdFromRedis(userId);
        if(CollectionUtils.isNotEmpty(friendIds)){
            return friendIds;
        }

        // 从数据库中获取好友id列表
        List<FriendRelation> friendRelations = baseMapper.selectList(new LambdaQueryWrapper<FriendRelation>()
                .eq(FriendRelation::getUserId, userId));
        if(CollectionUtils.isEmpty(friendRelations)){
            return friendIds;
        }

        friendIds = friendRelations.stream()
                .map(friendRelation -> friendRelation.getFriendId())
                .collect(Collectors.toList());

        return friendIds;
    }

    /**
     * 从redis中获取好友id列表
     * @author wangjinfei
     * @date 2025/7/28 20:44
     * @param userId
     * @return List<String>
    */
    private List<String> getFriendIdsByUserIdFromRedis(String userId) {
        List<String> friendIds = new ArrayList<>();

        Set<Object> friendIdsInRedis = redisUtil.zsReverseRange(RedisEnum.FRIEND_RELATION.getKey() + userId, 0, -1);
        if(CollectionUtils.isNotEmpty(friendIdsInRedis)){
            friendIds = friendIdsInRedis.stream()
                    .map(friendId -> friendId.toString())
                    .collect(Collectors.toList());
        }

        return friendIds;
    }

    /**
     * 从数据库获取好友列表
     * @author wangjinfei
     * @date 2025/8/9 11:59
     * @param userId
     * @return List<User>
     */
    @Override
    public List<User> getFriendsByUserId4Db(String userId) {
        List<FriendRelation> friendRelations = baseMapper.selectList(new LambdaQueryWrapper<FriendRelation>()
                .eq(FriendRelation::getUserId, userId)
                .select(FriendRelation::getFriendId));
        if(CollectionUtils.isEmpty(friendRelations)){
            return List.of();
        }

        List<String> friendIds = friendRelations.stream().map(FriendRelation::getFriendId).collect(Collectors.toList());
        List<User> friends = userService.listByIds(friendIds);
        return friends;
    }
}
