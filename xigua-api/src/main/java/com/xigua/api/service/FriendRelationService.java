package com.xigua.api.service;

import com.xigua.domain.dto.FriendVerifyDTO;
import com.xigua.domain.dto.sendFriendRequestDTO;
import com.xigua.domain.entity.User;
import com.xigua.domain.vo.FriendDetailVO;
import com.xigua.domain.vo.FriendVO;
import com.xigua.domain.vo.FriendRequestVO;

import java.util.List;

/**
 * @ClassName FriendRelationService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/13 21:24
 */
public interface FriendRelationService {
    /**
     * 发送好友请求
     * @author wangjinfei
     * @date 2025/5/13 21:26
     * @param dto
     * @return Boolean
     */
    Boolean sendFriendRequest(sendFriendRequestDTO dto);

    /**
     * 好友验证
     * @author wangjinfei
     * @date 2025/5/14 22:00
     * @param dto
     * @return Boolean
    */
    Boolean friendVerify(FriendVerifyDTO dto);

    /**
     * 好友详情
     * @author wangjinfei
     * @date 2025/5/17 12:06
     * @param friendId
     * @return FriendDetailVO
    */
    FriendDetailVO getFriendDetail(String friendId);

    /**
     * 所有好友关系存入redis
     * @author wangjinfei
     * @date 2025/6/2 20:58
    */
    void allFriendRelationToRedis();

    /**
     * 获取好友数量
     * @author wangjinfei
     * @date 2025/7/27 9:42
     * @param userId
     * @return Integer
     */
    Integer getCountByUserId(String userId);

    /**
     * 获取好友id列表
     * @author wangjinfei
     * @date 2025/7/27 10:50
     * @param userId
     * @return List<String>
    */
    List<String> getFriendIdsByUserId(String userId);

    /**
     * 从数据库获取好友列表
     * @author wangjinfei
     * @date 2025/8/9 11:59
     * @param userId
     * @return List<User>
    */
    List<User> getFriendsByUserId4Db(String userId);

    /**
     * 删除好友
     * @author wangjinfei
     * @date 2025/9/1 20:48
     * @param friendId
     * @return Boolean
    */
    Boolean delFriend(String friendId);

    /**
     * 是否好友
     * @author wangjinfei
     * @date 2025/9/13 17:41
     * @param userId
     * @param friendId
     * @return Boolean
    */
    Boolean isFriend(String userId, String friendId);
}
