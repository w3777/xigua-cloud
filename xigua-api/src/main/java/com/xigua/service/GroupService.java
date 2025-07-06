package com.xigua.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.dto.GroupDTO;
import com.xigua.domain.entity.Group;

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
    Boolean createGroup(GroupDTO dto);
}
