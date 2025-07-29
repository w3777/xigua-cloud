package com.xigua.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.dto.GroupDTO;
import com.xigua.domain.entity.Group;

import java.util.List;

/**
 * @ClassName GroupService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/6 10:59
 */
public interface GroupService extends IService<Group> {

    /**
     * 创建群组
     * @author wangjinfei
     * @date 2025/7/6 11:07
     * @param dto
     * @return Boolean
    */
    Boolean createGroup(GroupDTO dto) throws Exception;

    /**
     * 群组添加到缓存
     * @author wangjinfei
     * @date 2025/7/28 15:28
     * @param groupId
     * @return Boolean
    */
    Boolean addGroup2Redis(String groupId);

    /**
     * 获取所有群组id
     * @author wangjinfei
     * @date 2025/7/29 17:50
     * @return List<String>
     */
    List<String> getAllGroupId();
}
