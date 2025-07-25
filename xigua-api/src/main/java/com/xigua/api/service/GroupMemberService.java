package com.xigua.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.entity.GroupMember;

import java.util.List;

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
}
