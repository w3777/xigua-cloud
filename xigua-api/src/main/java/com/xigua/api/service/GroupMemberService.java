package com.xigua.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.entity.GroupMember;

import java.util.List;
import java.util.Set;

/**
 * @ClassName GroupMemberService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/6 11:14
 */
public interface GroupMemberService extends IService<GroupMember> {

    /**
     * 添加群成员
     * @author wangjinfei
     * @date 2025/7/6 11:27
     * @param groupId
     * @param memberIds
     * @return Boolean
    */
    Boolean addGroupMember(String groupId, List<String> memberIds);

    /**
     * 获取群成员数量
     * @author wangjinfei
     * @date 2025/7/27 9:42
     * @param userId
     * @return Integer
     */
    Integer getCountByUserId(String userId);

    /**
     * 获取加入的群列表
     * @author wangjinfei
     * @date 2025/7/27 11:08
     * @param userId
     * @return Set<String>
    */
    Set<String> getGroupIdsByUserId(String userId);

    /**
     * 获取群成员列表
     * @author wangjinfei
     * @date 2025/7/27 11:21
     * @param groupId
     * @return List<GroupMember>
    */
    List<GroupMember> getGroupMembersByGroupId(String groupId);
}
