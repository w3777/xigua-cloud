package com.xigua.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.entity.FriendRequest;

import java.util.List;

/**
 * @ClassName FriendRequestService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/13 21:22
 */
public interface FriendRequestService extends IService<FriendRequest> {

    /**
     * 查询发送的好友请求
     * @author wangjinfei
     * @date 2025/5/13 23:34
     * @param userId
     * @return List<FriendRequest>
     */
    List<FriendRequest> getListBySenderId(String userId);

    /**
     * 查询接收的好友请求
     * @author wangjinfei
     * @date 2025/5/13 23:34
     * @param userId
     * @return List<FriendRequest>
    */
    List<FriendRequest> getListByReceiverId(String userId);

    /**
     * 检查好友请求
     * @author wangjinfei
     * @date 2025/5/14 21:52
     * @param userId
     * @param friendId
    */
    void checkFriendRequest(String userId, String friendId);

    /**
     * 修改好友请求状态
     * @author wangjinfei
     * @date 2025/5/14 22:13
     * @param friendRequest
     * @return Boolean
    */
    Boolean updateFriendRequestStatus(FriendRequest friendRequest);

    /**
     * 根据发送者ID、接收者ID和状态查询好友请求
     * @author wangjinfei
     * @date 2025/5/14 22:16
     * @param senderId
     * @param receiverId
     * @param status
     * @return FriendRequest
    */
    FriendRequest getBySenderIdAndReceiverIdAndStatus(String senderId, String receiverId, Integer status);
}
