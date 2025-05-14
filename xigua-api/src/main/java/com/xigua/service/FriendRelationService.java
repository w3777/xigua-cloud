package com.xigua.service;

import com.xigua.domain.dto.FriendVerifyDTO;
import com.xigua.domain.dto.sendFriendRequestDTO;
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
     * 好友请求(包含已发送和已接收)
     * @author wangjinfei
     * @date 2025/5/13 23:10
     * @return ReceiveFriendRequestVO
    */
    List<FriendRequestVO> friendRequest();

    /**
     * 获取好友列表
     * @author wangjinfei
     * @date 2025/5/14 20:56
     * @return List<FriendVO>
    */
    List<FriendVO> getFriendList();

    /**
     * 好友验证
     * @author wangjinfei
     * @date 2025/5/14 22:00
     * @param dto
     * @return Boolean
    */
    Boolean friendVerify(FriendVerifyDTO dto);
}
