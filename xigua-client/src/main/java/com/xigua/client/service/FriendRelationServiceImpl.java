package com.xigua.client.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.client.mapper.FriendRelationMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.dto.FriendVerifyDTO;
import com.xigua.domain.dto.sendFriendRequestDTO;
import com.xigua.domain.entity.FriendRelation;
import com.xigua.domain.entity.FriendRequest;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.FriendRequestFlowStatus;
import com.xigua.domain.enums.MessageType;
import com.xigua.domain.enums.UserConnectStatus;
import com.xigua.domain.vo.FriendDetailVO;
import com.xigua.domain.vo.FriendVO;
import com.xigua.domain.vo.FriendRequestVO;
import com.xigua.service.CenterService;
import com.xigua.service.FriendRelationService;
import com.xigua.service.FriendRequestService;
import com.xigua.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName FriendRelationServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/13 21:24
 */
@DubboService
@RequiredArgsConstructor
public class FriendRelationServiceImpl extends ServiceImpl<FriendRelationMapper, FriendRelation> implements FriendRelationService {
    private final Sequence sequence;
    private final FriendRequestService friendRequestService;
    private final UserService userService;
    private final CenterService centerService;

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

        // 检查好友请求
        friendRequestService.checkFriendRequest(userId, friendId);
        // 检查好友关系
        checkFriendRelation(userId, friendId);

        // 添加好友申请
        FriendRequest friendRequest = new FriendRequest();
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
     * 好友请求(包含已发送和已接收)
     * @author wangjinfei
     * @date 2025/5/13 23:10
     * @return ReceiveFriendRequestVO
     */
    @Override
    public List<FriendRequestVO> friendRequest() {
        String userId = UserContext.get().getUserId();
        List<FriendRequestVO> voList = new ArrayList<>();
        List<String> userIdList = new ArrayList<>();

        // 查询发送的好友请求
        List<FriendRequest> senderFriendReqList = friendRequestService.getListBySenderId(userId);
        if(CollectionUtils.isNotEmpty(senderFriendReqList)){
            List<String> receiverIdList = senderFriendReqList.stream()
                    .map(friend -> friend.getReceiverId())
                    .collect(Collectors.toList());
            userIdList.addAll(receiverIdList);
        }

        // 查询接收的好友请求
        List<FriendRequest> receiverFriendReqList = friendRequestService.getListByReceiverId(userId);
        if(CollectionUtils.isNotEmpty(receiverFriendReqList)){
            List<String> senderIdList = receiverFriendReqList.stream()
                    .map(friend -> friend.getSenderId())
                    .collect(Collectors.toList());
            userIdList.addAll(senderIdList);
        }

        // 批量查询用户信息
        List<User> userList = userService.getListByIds(userIdList);

        // 映射发送好友请求vo字段
        for (FriendRequest friendRequest : senderFriendReqList) {
            User receiver = userList.stream()
                    .filter(user -> user.getId().equals(friendRequest.getReceiverId()))
                    .findFirst().orElse(null);
            if(receiver == null){
                continue;
            }
            FriendRequestVO friendRequestVO = new FriendRequestVO();
            friendRequestVO.setUserId(receiver.getId());
            friendRequestVO.setUsername(receiver.getUsername());
            friendRequestVO.setAvatar(receiver.getAvatar());
            friendRequestVO.setSignature(receiver.getSignature());
            friendRequestVO.setSource("send");

            friendRequestVO.setFlowStatus(friendRequest.getFlowStatus());
            friendRequestVO.setCreateTime(DateUtil.formatDateTime(friendRequest.getCreateTime(),
                    DateUtil.DATE_TIME_FORMATTER));
            voList.add(friendRequestVO);
        }

        // 映射接收好友请求vo字段
        for (FriendRequest friendRequest : receiverFriendReqList) {
            User sender = userList.stream()
                    .filter(user -> user.getId().equals(friendRequest.getSenderId()))
                    .findFirst().orElse(null);
            if(sender == null){
                continue;
            }
            FriendRequestVO friendRequestVO = new FriendRequestVO();
            friendRequestVO.setUserId(sender.getId());
            friendRequestVO.setUsername(sender.getUsername());
            friendRequestVO.setAvatar(sender.getAvatar());
            friendRequestVO.setSignature(sender.getSignature());
            friendRequestVO.setSource("receive");

            friendRequestVO.setFlowStatus(friendRequest.getFlowStatus());
            friendRequestVO.setCreateTime(DateUtil.formatDateTime(friendRequest.getCreateTime(),
                    DateUtil.DATE_TIME_FORMATTER));
            voList.add(friendRequestVO);
        }
        return voList;
    }

    /**
     * 获取好友列表
     * @author wangjinfei
     * @date 2025/5/14 20:56
     * @return List<FriendVO>
     */
    @Override
    public List<FriendVO> getFriendList() {
        String userId = UserContext.get().getUserId();
        List<FriendVO> voList = new ArrayList<>();

        // 查询好友请求
        List<FriendRelation> friendRelationList = baseMapper.selectList(new LambdaUpdateWrapper<FriendRelation>()
                .eq(FriendRelation::getUserId,userId));
        if(CollectionUtils.isEmpty(friendRelationList)){
            return voList;
        }

        // 查询用户信息
        List<String> senderIdList = friendRelationList.stream()
                .map(friend -> friend.getFriendId())
                .collect(Collectors.toList());
        List<User> userList = userService.getListByIds(senderIdList);

        // 映射vo字段
        for (User user : userList) {
            String userId2 = user.getId();
            FriendVO friendVO = new FriendVO();
            friendVO.setUserId(userId2);
            friendVO.setUsername(user.getUsername());
            friendVO.setAvatar(user.getAvatar());
            friendVO.setSignature(user.getSignature());

            // 判断是否在线
            friendVO.setIsOnline(centerService.isOnline(userId2));
            voList.add(friendVO);
        }
        return voList;
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
            acceptFriendRequest(userId,friendId);
        }else if(flowStatus == FriendRequestFlowStatus.TWO.getStatus()){ // 拒绝
            rejectFriendRequest(userId, friendId);
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
    private void acceptFriendRequest(String userId, String friendId) {
        // 查询待验证的好友请求
        FriendRequest friendRequest = friendRequestService.getLastOne(friendId, userId);
        if(friendRequest == null){
            throw new BusinessException("待验证的好友请求不存在");
        }

        // 直接同意好友请求
        if(friendRequest.getFlowStatus() == FriendRequestFlowStatus.ZERO.getStatus()){
            directAccept(userId, friendId);
        }

        // 上一次请求状态是拒绝，重新验证好友
        if(friendRequest.getFlowStatus() == FriendRequestFlowStatus.TWO.getStatus()){
            againFriendVerify(userId, friendId);
        }
    }

    /**
     * 直接同意好友请求
     * @author wangjinfei
     * @date 2025/5/16 20:09
     * @param userId
     * @param friendId
    */
    private void directAccept(String userId, String friendId) {
        // 把之前的好友请求状态修改为失效
        friendRequestService.updateFlowStatus2Invalid(friendId, userId);

        // 确认好友关系以及默认消息
        friendDbHandle(userId, friendId);
        // 角色反转 确认好友关系以及默认消息
        friendDbHandle(friendId, userId);
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
        // 添加好友请求状态为通过
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(sequence.nextNo());
        friendRequest.setSenderId(userId);
        friendRequest.setReceiverId(friendId);
        friendRequest.setFlowStatus(FriendRequestFlowStatus.ONE.getStatus());
        friendRequestService.save(friendRequest);

        // 添加好友关系
        FriendRelation friendRelation = new FriendRelation();
        friendRelation.setId(sequence.nextNo());
        friendRelation.setUserId(userId);
        friendRelation.setFriendId(friendId);
        boolean save = save(friendRelation);

        // 添加好友默认发送消息
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderId(userId);
        chatMessageDTO.setReceiverId(friendId);
        chatMessageDTO.setMessageType(MessageType.CHAT.getType());
        chatMessageDTO.setMessage("你已成为我的好友");
        chatMessageDTO.setCreateTime(String.valueOf(System.currentTimeMillis()));
        centerService.receiveMessage4Client(chatMessageDTO);
    }

    /**
     * 重新验证好友
     * @author wangjinfei
     * @date 2025/5/16 20:13
     * @param userId
     * @param friendId
    */
    private void againFriendVerify(String userId, String friendId) {
        // 把之前的好友请求状态修改为失效
        friendRequestService.updateFlowStatus2Invalid(friendId, userId);

        // 添加好友请求状态为待验证
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(sequence.nextNo());
        friendRequest.setSenderId(userId);
        friendRequest.setReceiverId(friendId);
        friendRequest.setFlowStatus(FriendRequestFlowStatus.ZERO.getStatus());
        friendRequestService.save(friendRequest);
    }

    /**
     * 拒绝好友请求
     * @author wangjinfei
     * @date 2025/5/14 22:11
     * @param userId
     * @param friendId
    */
    private void rejectFriendRequest(String userId, String friendId){
        // 查询待验证的好友请求
        FriendRequest friendRequest = friendRequestService.getLastOne(friendId, userId);
        if(friendRequest == null){
            throw new BusinessException("待验证的好友请求不存在");
        }

        // 把之前的好友请求状态修改为失效
        friendRequestService.updateFlowStatus2Invalid(friendId, userId);

        // 添加好友请求状态为拒绝
        FriendRequest friendRequest2 = new FriendRequest();
        friendRequest2.setId(sequence.nextNo());
        friendRequest2.setSenderId(userId);
        friendRequest2.setReceiverId(friendId);
        friendRequest2.setFlowStatus(FriendRequestFlowStatus.TWO.getStatus());
        friendRequestService.save(friendRequest2);
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
}
