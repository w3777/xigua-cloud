package com.xigua.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.entity.GroupMember;

import java.time.LocalDateTime;
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
    List<GroupMember> getListByGroupId(String groupId);

    /**
     * 获取加入时间
     * @author wangjinfei
     * @date 2025/7/30 19:46
     * @param groupId
     * @param userId
     * @return LocalDateTime
    */
    LocalDateTime getJoinTime(String groupId, String userId);

    /**
     * 获取群角色
     * @author wangjinfei
     * @date 2025/7/30 19:59
     * @param groupId
     * @param userId
     * @return Integer
    */
    Integer getGroupRole(String groupId, String userId);

    /**
     * 根据群id获取群成员列表
     * @author wangjinfei
     * @date 2025/8/17 17:47
     * @param groupId
     * @return Set<String>
    */
    Set<String> getGroupMembers(String groupId);
}
