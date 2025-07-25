package com.xigua.api.service;

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
     * 修改好友请求流程状态为失效
     * @author wangjinfei
     * @date 2025/5/14 22:13
     * @param senderId
     * @param receiverId
     * @return Boolean
     */
    Boolean updateFlowStatus2Invalid(String senderId, String receiverId);

    /**
     * 根据发送者ID、接收者ID获取最后一条好友请求
     * @author wangjinfei
     * @date 2025/5/14 22:16
     * @param senderId
     * @param receiverId
     * @return FriendRequest
    */
    FriendRequest getLastOne(String senderId, String receiverId);
}
