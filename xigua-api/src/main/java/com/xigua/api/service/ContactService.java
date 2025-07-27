package com.xigua.api.service;

import com.xigua.domain.vo.ContactCountVO;
import com.xigua.domain.vo.FriendVO;
import com.xigua.domain.vo.GroupVO;

import java.util.List;

/**
 * @ClassName ContactService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/27 9:38
 */
public interface ContactService {

    /**
     * 获取联系人数量
     * @author wangjinfei
     * @date 2025/7/27 9:42
     * @return ContactCountVO
    */
    ContactCountVO getContactCount();

    /**
     * 获取好友列表
     * @author wangjinfei
     * @date 2025/5/14 20:56
     * @return List<FriendVO>
     */
    List<FriendVO> getFriendList();

    /**
     * 获取群列表
     * @author wangjinfei
     * @date 2025/7/27 11:44
     * @return List<GroupVO>
     */
    List<GroupVO> getGroupList();
}
